// SPDX-License-Identifier: MPL-2.0
/*
 * Android text to speech bridge that runs inside the Android runtime (ART) of the
 * launcher that hosts the Minecraft JVM.
 *
 * Ported from prism's AndroidTextToSpeechBackend
 * (source/backends/android/AndroidTextToSpeechBackend.java, MPL-2.0,
 * https://github.com/ethindp/prism) and adapted for minecraft-access:
 *
 *  - the djinni based prism interface is replaced by the small static API below,
 *    which the mod calls over JNI (see android/native/tts_bridge.c);
 *  - the TextToSpeech object lives on its own looper thread, because the game
 *    thread inside the JVM has no Looper and would never receive init or
 *    utterance callbacks;
 *  - rate and pitch arrive as 0..1 positions on every utterance instead of being
 *    stored state, so a configuration change takes effect on the next narration;
 *  - an engine that fails to initialise falls back to the other installed engines
 *    (prism reports the failure and leaves the choice to the caller).
 */
package org.mcaccess.minecraftaccess.android;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Static entry points called from the mod through JNI. Every method is safe to
 * call from a thread that the Android runtime has never seen before.
 */
public final class AndroidTtsBridge {
    private static final String TAG = "mcaccess-tts";
    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_INITIALIZING = 1;
    private static final int STATE_READY = 2;
    private static final int STATE_FAILED = 3;
    private static final int MAX_PENDING_SPEECHES = 64;

    private static final Object LOCK = new Object();
    private static final ArrayDeque<Speech> PENDING = new ArrayDeque<Speech>();
    private static final List<String> ENGINES = new ArrayList<String>();
    private static final List<String> TRIED_ENGINES = new ArrayList<String>();

    private static volatile int state = STATE_UNINITIALIZED;
    private static volatile TextToSpeech tts;
    private static volatile Handler handler;
    private static volatile HandlerThread thread;
    private static volatile Locale locale;
    private static volatile String lastError = "";

    private static Context context;
    private static CharsetDecoder decoder;
    private static float appliedRate = Float.NaN;
    private static float appliedPitch = Float.NaN;
    private static int utteranceCounter;

    private AndroidTtsBridge() {
    }

    /**
     * Creates the engine. Returns true when initialisation has started; speech
     * requested before the engine is ready is queued.
     *
     * @param androidContext application context of the launcher
     * @param languageTag BCP-47 tag the game is running in, may be null
     */
    public static boolean start(Context androidContext, String languageTag) {
        synchronized (LOCK) {
            if (state == STATE_READY || state == STATE_INITIALIZING) {
                return true;
            }
            if (androidContext == null) {
                state = STATE_FAILED;
                lastError = "no Android context available";
                Log.e(TAG, lastError);
                return false;
            }
            Context application = androidContext.getApplicationContext();
            context = application != null ? application : androidContext;
            locale = parseLocale(languageTag);
            decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);
            ENGINES.clear();
            TRIED_ENGINES.clear();
            PENDING.clear();
            HandlerThread looperThread = new HandlerThread("mcaccess-tts");
            looperThread.start();
            thread = looperThread;
            handler = new Handler(looperThread.getLooper());
            state = STATE_INITIALIZING;
            handler.post(() -> construct(null));
        }
        Log.i(TAG, "starting Android text to speech (language="
                + (locale == null ? "device default" : locale.toLanguageTag()) + ")");
        return true;
    }

    /**
     * Queues a narration.
     *
     * @param utf8 narration text encoded as UTF-8, the mod sends bytes so that
     *             no modified UTF-8 conversion can corrupt CJK or emoji
     * @param ratePosition speech rate as a 0..1 position, 0.5 is the natural speed
     * @param pitchPosition speech pitch as a 0..1 position, 0.5 is the natural pitch
     * @param interrupt whether the queued speech has to be dropped
     * @return 0 when the text was accepted, -1 when the engine is unusable
     */
    public static int speak(byte[] utf8, float ratePosition, float pitchPosition, boolean interrupt) {
        if (utf8 == null || utf8.length == 0) {
            return -1;
        }
        String text = decode(utf8);
        if (text == null || text.trim().isEmpty()) {
            return -1;
        }
        final Speech speech = new Speech(text, ratePosition, pitchPosition, interrupt);
        synchronized (LOCK) {
            if (state == STATE_FAILED) {
                return -1;
            }
            if (state == STATE_READY && tts != null && handler != null) {
                handler.post(() -> speakNow(speech));
                return 0;
            }
            if (interrupt) {
                PENDING.clear();
            }
            while (PENDING.size() >= MAX_PENDING_SPEECHES) {
                PENDING.removeFirst();
            }
            PENDING.addLast(speech);
            return 0;
        }
    }

    /** Drops everything that is queued or being spoken. */
    public static void stop() {
        final TextToSpeech instance;
        final Handler looper;
        synchronized (LOCK) {
            PENDING.clear();
            instance = tts;
            looper = handler;
        }
        if (instance == null || looper == null) {
            return;
        }
        looper.post(() -> {
            try {
                instance.stop();
            } catch (RuntimeException e) {
                Log.w(TAG, "stop failed", e);
            }
        });
    }

    /** @return true when the engine is currently producing speech */
    public static boolean isSpeaking() {
        TextToSpeech instance = tts;
        if (state != STATE_READY || instance == null) {
            return false;
        }
        try {
            return instance.isSpeaking();
        } catch (RuntimeException e) {
            Log.w(TAG, "isSpeaking failed", e);
            return false;
        }
    }

    /** Releases the engine and stops its looper thread. */
    public static void shutdown() {
        final TextToSpeech instance;
        final Handler looper;
        final HandlerThread looperThread;
        synchronized (LOCK) {
            state = STATE_UNINITIALIZED;
            PENDING.clear();
            instance = tts;
            tts = null;
            looper = handler;
            handler = null;
            looperThread = thread;
            thread = null;
            decoder = null;
            appliedRate = Float.NaN;
            appliedPitch = Float.NaN;
        }
        if (looper != null) {
            looper.post(() -> release(instance));
        }
        if (looperThread != null) {
            looperThread.quitSafely();
        }
        Log.i(TAG, "Android text to speech shut down");
    }

    /** @return the last problem the bridge ran into, for the mod log */
    public static String lastError() {
        return lastError;
    }

    private static void construct(final String engineName) {
        final TextToSpeech[] holder = new TextToSpeech[1];
        TextToSpeech.OnInitListener listener = status -> {
            synchronized (LOCK) {
                if (state != STATE_INITIALIZING) {
                    return;
                }
            }
            if (status == TextToSpeech.SUCCESS) {
                onEngineReady(holder[0], engineName);
            } else {
                lastError = "engine init failed: " + describe(engineName) + ", status=" + status;
                Log.e(TAG, lastError);
                tryNextEngine(holder[0], engineName);
            }
        };
        try {
            TextToSpeech instance = (engineName == null)
                    ? new TextToSpeech(context, listener)
                    : new TextToSpeech(context, listener, engineName);
            holder[0] = instance;
            rememberEngines(instance);
        } catch (RuntimeException e) {
            lastError = "creating TextToSpeech failed: " + e;
            Log.e(TAG, lastError, e);
            tryNextEngine(null, engineName);
        }
    }

    private static void onEngineReady(TextToSpeech instance, String engineName) {
        if (instance == null) {
            lastError = "TextToSpeech was not created: " + describe(engineName);
            Log.e(TAG, lastError);
            tryNextEngine(null, engineName);
            return;
        }
        try {
            Locale target = locale;
            instance.setLanguage(target != null ? target : Locale.getDefault());
            instance.setSpeechRate(1.0f);
            instance.setPitch(1.0f);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            instance.setAudioAttributes(attributes);
            instance.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                    lastError = "utterance failed: " + utteranceId;
                    Log.e(TAG, lastError);
                }

                @Override
                public void onError(String utteranceId, int errorCode) {
                    lastError = "utterance failed: " + utteranceId + ", errorCode=" + errorCode;
                    Log.e(TAG, lastError);
                }
            });
        } catch (RuntimeException e) {
            lastError = "configuring TextToSpeech failed: " + e;
            Log.e(TAG, lastError, e);
            release(instance);
            tryNextEngine(null, engineName);
            return;
        }
        synchronized (LOCK) {
            if (state != STATE_INITIALIZING) {
                release(instance);
                return;
            }
            tts = instance;
            appliedRate = Float.NaN;
            appliedPitch = Float.NaN;
            state = STATE_READY;
        }
        Log.i(TAG, "Android text to speech ready, engine=" + describe(engineName));
        drainPending();
    }

    private static void tryNextEngine(TextToSpeech failed, String engineName) {
        release(failed);
        String next = null;
        synchronized (LOCK) {
            TRIED_ENGINES.add(describe(engineName));
            for (String candidate : ENGINES) {
                if (!TRIED_ENGINES.contains(candidate)) {
                    next = candidate;
                    break;
                }
            }
            if (next == null) {
                state = STATE_FAILED;
                PENDING.clear();
            }
        }
        if (next == null) {
            Log.e(TAG, "no usable Android text to speech engine, tried=" + TRIED_ENGINES);
            return;
        }
        final String engine = next;
        Log.i(TAG, "falling back to Android text to speech engine: " + engine);
        Handler looper = handler;
        if (looper != null) {
            looper.post(() -> construct(engine));
        }
    }

    private static void drainPending() {
        List<Speech> batch;
        synchronized (LOCK) {
            if (PENDING.isEmpty()) {
                return;
            }
            batch = new ArrayList<Speech>(PENDING);
            PENDING.clear();
        }
        for (Speech speech : batch) {
            speakNow(speech);
        }
    }

    private static void speakNow(Speech speech) {
        TextToSpeech instance = tts;
        if (state != STATE_READY || instance == null) {
            return;
        }
        try {
            applyRateAndPitch(instance, speech.ratePosition, speech.pitchPosition);
            Bundle params = new Bundle();
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);
            int maxLength = TextToSpeech.getMaxSpeechInputLength();
            List<String> segments = speech.text.length() >= maxLength
                    ? TextChunker.split(CharBuffer.wrap(speech.text), maxLength, maxLength)
                    : Collections.singletonList(speech.text);
            String prefix = "mcaccess-" + (++utteranceCounter) + "-";
            for (int index = 0; index < segments.size(); index++) {
                String segment = segments.get(index);
                int queueMode = index == 0 && speech.interrupt
                        ? TextToSpeech.QUEUE_FLUSH
                        : TextToSpeech.QUEUE_ADD;
                String utteranceId = prefix + index;
                if (instance.speak(segment, queueMode, params, utteranceId) != TextToSpeech.SUCCESS) {
                    lastError = "queueing speech failed: " + utteranceId;
                    Log.e(TAG, lastError);
                    return;
                }
            }
            Log.i(TAG, "narration (interrupt=" + speech.interrupt + "): " + speech.text);
        } catch (RuntimeException e) {
            lastError = "speaking failed: " + e;
            Log.e(TAG, lastError, e);
        }
    }

    private static void applyRateAndPitch(TextToSpeech instance, float ratePosition, float pitchPosition) {
        float rate = Utils.expRangeConvert(ratePosition, 0.1f, 1.0f, 6.0f);
        if (Float.isNaN(appliedRate) || Math.abs(rate - appliedRate) > 0.001f) {
            if (instance.setSpeechRate(rate) == TextToSpeech.ERROR) {
                Log.w(TAG, "setSpeechRate failed: " + rate);
            } else {
                appliedRate = rate;
            }
        }
        float pitch = Utils.expRangeConvert(pitchPosition, 0.25f, 1.0f, 4.0f);
        if (Float.isNaN(appliedPitch) || Math.abs(pitch - appliedPitch) > 0.001f) {
            if (instance.setPitch(pitch) == TextToSpeech.ERROR) {
                Log.w(TAG, "setPitch failed: " + pitch);
            } else {
                appliedPitch = pitch;
            }
        }
    }

    private static void rememberEngines(TextToSpeech instance) {
        try {
            List<TextToSpeech.EngineInfo> installed = instance.getEngines();
            if (installed == null) {
                return;
            }
            for (TextToSpeech.EngineInfo info : installed) {
                if (info != null && info.name != null && !ENGINES.contains(info.name)) {
                    ENGINES.add(info.name);
                }
            }
        } catch (RuntimeException e) {
            Log.w(TAG, "unable to list the installed text to speech engines", e);
        }
    }

    private static void release(TextToSpeech instance) {
        if (instance == null) {
            return;
        }
        try {
            instance.stop();
        } catch (RuntimeException ignored) {
            // the engine is being torn down anyway
        }
        try {
            instance.shutdown();
        } catch (RuntimeException ignored) {
            // the engine is being torn down anyway
        }
    }

    private static String decode(byte[] utf8) {
        CharsetDecoder candidate = decoder;
        if (candidate == null) {
            return new String(utf8, StandardCharsets.UTF_8);
        }
        try {
            synchronized (candidate) {
                candidate.reset();
                return candidate.decode(ByteBuffer.wrap(utf8)).toString();
            }
        } catch (CharacterCodingException e) {
            Log.w(TAG, "malformed narration text, decoding leniently", e);
            return new String(utf8, StandardCharsets.UTF_8);
        }
    }

    private static Locale parseLocale(String languageTag) {
        if (languageTag == null || languageTag.isEmpty()) {
            return null;
        }
        Locale parsed = Locale.forLanguageTag(languageTag.replace('_', '-'));
        if (parsed.getLanguage().isEmpty()) {
            return null;
        }
        return parsed;
    }

    private static String describe(String engineName) {
        return engineName == null ? "default" : engineName;
    }

    private static final class Speech {
        private final String text;
        private final float ratePosition;
        private final float pitchPosition;
        private final boolean interrupt;

        private Speech(String text, float ratePosition, float pitchPosition, boolean interrupt) {
            this.text = text;
            this.ratePosition = ratePosition;
            this.pitchPosition = pitchPosition;
            this.interrupt = interrupt;
        }
    }
}
