// SPDX-License-Identifier: MPL-2.0
/*
 * Ported from prism (source/backends/android/TextChunker.java, MPL-2.0,
 * https://github.com/ethindp/prism). Only the Java 9+/API 30+ collection
 * factories were replaced (List.of/List.copyOf) and `var` was expanded so the
 * class also runs on older Android versions.
 */
package org.mcaccess.minecraftaccess.android;

import android.icu.text.BreakIterator;
import android.icu.util.ULocale;

import java.nio.CharBuffer;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TextChunker {
    private TextChunker() {
    }

    public static List<String> split(CharBuffer input, ULocale locale, int targetChars, int maxChars) {
        Objects.requireNonNull(input, "input");
        ULocale effectiveLocale = (locale != null) ? locale : ULocale.getDefault();
        int target = Math.max(1, targetChars);
        int max = Math.max(target, maxChars);
        CharBuffer ro = input.asReadOnlyBuffer();
        int base = ro.position();
        int lim = ro.limit();
        int len = lim - base;
        if (len <= 0) {
            return Collections.emptyList();
        }
        Cache cache = CACHE.get();
        cache.ensureLocale(effectiveLocale);
        CharBufferCharacterIterator textIt = new CharBufferCharacterIterator(ro, base, lim);
        BreakIterator sentence = cache.sentence;
        BreakIterator line = cache.line;
        BreakIterator character = cache.character;
        sentence.setText(textIt);
        line.setText(textIt);
        character.setText(textIt);
        int approx = Math.max(1, (len + target - 1) / target);
        ArrayList<String> out = new ArrayList<>(approx);
        char[] scratch = new char[Math.min(max, len)];
        CharBuffer reader = ro.duplicate();
        int i = 0;
        while (i < len) {
            char c = textIt.charAtLocal(i);
            if (!(c <= ' ' || Character.isWhitespace(c))) {
                break;
            }
            i++;
        }
        while (i < len) {
            int start = i;
            int hard = Math.min(start + max, len);
            int ideal = Math.min(start + target, hard);
            int cut = pickBestBoundary(sentence, start, ideal, hard);
            if (cut <= start) {
                cut = pickBestBoundary(line, start, ideal, hard);
            }
            if (cut <= start) {
                cut = safeGraphemeCut(character, start, hard, len);
            }
            int end = cut;
            while (end > start) {
                char c = textIt.charAtLocal(end - 1);
                if (!(c <= ' ' || Character.isWhitespace(c))) {
                    break;
                }
                end--;
            }
            if (end > start) {
                if (ro.hasArray()) {
                    char[] a = ro.array();
                    int off = ro.arrayOffset();
                    out.add(new String(a, off + base + start, end - start));
                } else {
                    reader.position(base + start);
                    reader.get(scratch, 0, end - start);
                    out.add(new String(scratch, 0, end - start));
                }
            }
            i = cut;
            while (i < len) {
                char c = textIt.charAtLocal(i);
                if (!(c <= ' ' || Character.isWhitespace(c))) {
                    break;
                }
                i++;
            }
        }
        return Collections.unmodifiableList(out);
    }

    public static List<String> split(CharBuffer input, int targetChars, int maxChars) {
        return split(input, ULocale.getDefault(), targetChars, maxChars);
    }

    private static int pickBestBoundary(BreakIterator it, int start, int ideal, int hard) {
        int before = -1;
        int b = it.following(start);
        if (b == BreakIterator.DONE || b > hard) {
            return -1;
        }
        while (b != BreakIterator.DONE && b <= hard) {
            if (b >= ideal) {
                return b;
            }
            before = b;
            b = it.next();
        }
        return (before > start) ? before : -1;
    }

    private static int safeGraphemeCut(BreakIterator character, int start, int hard, int len) {
        int cut = hard;
        if (cut >= len) {
            return len;
        }
        int prev = character.preceding(cut);
        if (prev != BreakIterator.DONE && prev > start) {
            return prev;
        }
        int next = character.following(start);
        if (next != BreakIterator.DONE && next > start) {
            return Math.min(next, len);
        }
        return Math.min(start + 1, len);
    }

    private static final class Cache {
        private ULocale locale;
        private BreakIterator sentence;
        private BreakIterator line;
        private BreakIterator character;

        void ensureLocale(ULocale loc) {
            if (locale != null && locale.equals(loc)) {
                return;
            }
            locale = loc;
            sentence = BreakIterator.getSentenceInstance(loc);
            line = BreakIterator.getLineInstance(loc);
            character = BreakIterator.getCharacterInstance(loc);
        }
    }

    private static final ThreadLocal<Cache> CACHE = new ThreadLocal<Cache>() {
        @Override
        protected Cache initialValue() {
            return new Cache();
        }
    };

    private static final class CharBufferCharacterIterator implements CharacterIterator, Cloneable {
        private final CharBuffer buf;
        private final int absBegin;
        private final int absEnd;
        private final int len;
        private int index;

        CharBufferCharacterIterator(CharBuffer buf, int absBegin, int absEnd) {
            this.buf = buf;
            this.absBegin = absBegin;
            this.absEnd = absEnd;
            this.len = absEnd - absBegin;
            this.index = 0;
        }

        char charAtLocal(int localIndex) {
            if (localIndex < 0 || localIndex >= len) {
                return CharacterIterator.DONE;
            }
            return buf.get(absBegin + localIndex);
        }

        @Override
        public char first() {
            index = 0;
            return current();
        }

        @Override
        public char last() {
            index = (len == 0) ? 0 : (len - 1);
            return current();
        }

        @Override
        public char current() {
            if (index < 0 || index >= len) {
                return CharacterIterator.DONE;
            }
            return buf.get(absBegin + index);
        }

        @Override
        public char next() {
            if (index >= len) {
                index = len;
                return CharacterIterator.DONE;
            }
            index++;
            return current();
        }

        @Override
        public char previous() {
            if (index <= 0) {
                index = 0;
                return CharacterIterator.DONE;
            }
            index--;
            return current();
        }

        @Override
        public char setIndex(int position) {
            if (position < 0 || position > len) {
                throw new IllegalArgumentException("position");
            }
            index = position;
            return current();
        }

        @Override
        public int getBeginIndex() {
            return 0;
        }

        @Override
        public int getEndIndex() {
            return len;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
}
