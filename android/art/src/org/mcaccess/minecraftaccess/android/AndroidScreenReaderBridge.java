package org.mcaccess.minecraftaccess.android;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Speaks through the screen reader that is already running inside Android,
 * instead of synthesising speech ourselves, so that a TalkBack user keeps their
 * own voice, rate, pitch and language.
 *
 * <p>This is a port of the Android screen reader backend of
 * <a href="https://github.com/ethindp/prism">prism</a> (MPL-2.0): the same
 * {@code AccessibilityManager} availability check, the same
 * {@code TYPE_ANNOUNCEMENT} event and the same service naming.
 *
 * <p>This class lives in the launcher's process and is reached from the game's
 * JVM over JNI, so it follows two rules. Nothing may throw back into C, where
 * an exception is at best cleared and the call lost. And work posted to the
 * thread below has to catch everything itself: that thread belongs to the
 * launcher, and an exception that escapes from it ends the whole application.
 */
public final class AndroidScreenReaderBridge {
    private static final String TAG = "mcaccess-tts";
    private static final Object LOCK = new Object();
    private static final int DUPLICATE_SPEECH_HACK_LIMIT = 100;
    private static Context context;
    private static AccessibilityManager manager;
    private static HandlerThread thread;
    private static Handler handler;
    private static String lastError = "";
    /**
     * Android drops an announcement whose text is identical to the previous
     * one, and TalkBack would then stay silent. prism appends an ever growing
     * run of spaces to keep the events apart; the counter restarts once the
     * padding grows silly so that the text stays readable in a log.
     */
    private static String duplicateSpeechHack = "";

    private AndroidScreenReaderBridge() {
    }

    /**
     * Binds to the running screen reader.
     *
     * @param androidContext application context of the launcher
     * @return true when a spoken accessibility service is enabled
     */
    public static boolean start(Context androidContext) {
        synchronized (LOCK) {
            if (manager != null) return true;
            if (androidContext == null) {
                lastError = "no Android context available";
                Log.e(TAG, lastError);
                return false;
            }
            Context application = androidContext.getApplicationContext();
            Context app = application != null ? application : androidContext;
            AccessibilityManager accessible =
                    (AccessibilityManager) app.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (accessible == null) {
                lastError = "the launcher did not expose an AccessibilityManager";
                Log.e(TAG, lastError);
                return false;
            }
            if (!accessible.isEnabled()) {
                lastError = "accessibility is turned off";
                Log.i(TAG, lastError);
                return false;
            }
            List<AccessibilityServiceInfo> services =
                    accessible.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
            if (services == null || services.isEmpty()) {
                lastError = "no spoken accessibility service is enabled";
                Log.i(TAG, lastError);
                return false;
            }
            // Build everything first and publish it afterwards, so that a
            // failure half way through cannot leave the bridge marked as
            // running with no thread behind it.
            HandlerThread looperThread = new HandlerThread("mcaccess-screen-reader");
            looperThread.start();
            Handler looperHandler = new Handler(looperThread.getLooper());
            context = app;
            manager = accessible;
            thread = looperThread;
            handler = looperHandler;
        }
        Log.i(TAG, "speaking through the system screen reader " + name());
        return true;
    }

    /** @return the description of the screen reader that is running, as prism reports it */
    public static String name() {
        Context ctx = context;
        AccessibilityManager accessible = manager;
        if (ctx == null || accessible == null) return "Screen reader";
        if (accessible.isEnabled()) {
            List<AccessibilityServiceInfo> services =
                    accessible.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
            if (services != null && !services.isEmpty()) {
                if (accessible.isTouchExplorationEnabled()) {
                    return services.get(0).loadDescription(ctx.getPackageManager());
                }
                for (AccessibilityServiceInfo info : services) {
                    if (info.getId() != null && info.getId().contains("com.nirenr.talkman")) {
                        return info.loadDescription(ctx.getPackageManager());
                    }
                }
            }
        }
        return "Screen reader";
    }

    /**
     * Queues an announcement.
     *
     * @param utf8 narration text encoded as UTF-8
     * @param interrupt whether the screen reader has to drop what it is saying
     * @return 0 when the text was accepted, -1 when no screen reader is bound
     */
    public static int announce(byte[] utf8, boolean interrupt) {
        if (utf8 == null || utf8.length == 0) return -1;
        final Context ctx = context;
        final AccessibilityManager accessible = manager;
        final Handler target = handler;
        if (ctx == null || accessible == null || target == null) return -1;
        final byte[] copy = utf8.clone();
        target.post(() -> {
            try {
                // Decoded afresh for every announcement, because a decoder that
                // has been flushed cannot be used again.
                String text = new String(copy, StandardCharsets.UTF_8);
                if (text.trim().isEmpty()) return;
                send(ctx, accessible, text, interrupt);
            } catch (Throwable e) {
                // The thread belongs to the launcher, and an exception that
                // escapes from here takes the whole application down.
                lastError = "announcing failed: " + e;
                Log.e(TAG, lastError, e);
            }
        });
        return 0;
    }

    private static void send(Context ctx, AccessibilityManager accessible, String text, boolean interrupt) {
        try {
            if (interrupt) accessible.interrupt();
            AccessibilityEvent event;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                event = new AccessibilityEvent();
            } else {
                event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            }
            event.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
            event.setPackageName(ctx.getPackageName());
            event.getText().add(text + duplicateSpeechHack);
            duplicateSpeechHack += " ";
            if (duplicateSpeechHack.length() > DUPLICATE_SPEECH_HACK_LIMIT) duplicateSpeechHack = "";
            accessible.sendAccessibilityEvent(event);
        } catch (RuntimeException e) {
            lastError = "sending the announcement failed: " + e;
            Log.e(TAG, lastError, e);
        }
    }

    /** Asks the screen reader to stop talking. */
    public static void stop() {
        AccessibilityManager accessible = manager;
        if (accessible == null) return;
        try {
            accessible.interrupt();
        } catch (RuntimeException e) {
            lastError = "interrupting the screen reader failed: " + e;
            Log.e(TAG, lastError, e);
        }
    }

    /** @return the last problem the bridge ran into */
    public static String lastError() {
        return lastError;
    }

    /** Unbinds from the screen reader. */
    public static void shutdown() {
        HandlerThread looperThread;
        synchronized (LOCK) {
            looperThread = thread;
            thread = null;
            handler = null;
            manager = null;
            context = null;
            duplicateSpeechHack = "";
        }
        if (looperThread != null) looperThread.quitSafely();
    }

}
