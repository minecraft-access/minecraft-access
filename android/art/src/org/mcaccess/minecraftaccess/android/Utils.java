// SPDX-License-Identifier: MPL-2.0
/*
 * Ported verbatim from prism (source/backends/android/utils.java, MPL-2.0,
 * https://github.com/ethindp/prism). Used by AndroidTtsBridge to map the
 * 0..1 rate/pitch positions onto the platform's multiplicative ranges.
 */
package org.mcaccess.minecraftaccess.android;

final class Utils {
    private Utils() {
    }

    public static float rangeConvert(float oldValue, float oldMin, float oldMax, float newMin, float newMax) {
        return ((oldValue - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    public static float rangeConvertMidpoint(float oldValue, float oldMin, float oldMidpoint, float oldMax,
                                             float newMin, float newMidpoint, float newMax) {
        if (oldValue <= oldMidpoint) {
            return rangeConvert(oldValue, oldMin, oldMidpoint, newMin, newMidpoint);
        }
        return rangeConvert(oldValue, oldMidpoint, oldMax, newMidpoint, newMax);
    }

    /**
     * Maps a 0..1 position onto an exponential curve through outMin, outMid and outMax,
     * so that the middle of the slider/percentage is the natural reading speed.
     */
    public static float expRangeConvert(float t, float outMin, float outMid, float outMax) {
        float clamped = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
        float logMin = (float) Math.log(outMin);
        float logMid = (float) Math.log(outMid);
        float logMax = (float) Math.log(outMax);
        return (float) Math.exp((clamped <= 0.5f)
                ? logMin + (logMid - logMin) * (clamped / 0.5f)
                : logMid + (logMax - logMid) * ((clamped - 0.5f) / 0.5f));
    }

    public static float expRangeConvertInv(float value, float outMin, float outMid, float outMax) {
        float v = value < outMin ? outMin : (value > outMax ? outMax : value);
        float logMin = (float) Math.log(outMin);
        float logMid = (float) Math.log(outMid);
        float logMax = (float) Math.log(outMax);
        float logValue = (float) Math.log(v);
        return (logValue <= logMid)
                ? 0.5f * (logValue - logMin) / (logMid - logMin)
                : 0.5f + (0.5f * (logValue - logMid) / (logMax - logMid));
    }
}
