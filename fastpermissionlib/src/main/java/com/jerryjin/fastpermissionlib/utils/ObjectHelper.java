package com.jerryjin.fastpermissionlib.utils;

import java.util.Collection;

public class ObjectHelper {

    public static <T> boolean isNotEmpty(T[] objects) {
        return nonNull(objects) && objects.length != 0;
    }

    public static <T> boolean isNotEmpty(Collection<T> tCollection) {
        return nonNull(tCollection) && tCollection.size() != 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean nonNull(Object object) {
        return object != null;
    }

    public static boolean isNotEmpty(int[] integers) {
        return integers != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(integers), 0) && integers.length != 0;
    }

    public static boolean isNotEmpty(byte[] bytes) {
        return bytes != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(bytes), 0) && bytes.length != 0;
    }

    public static boolean isNotEmpty(short[] shorts) {
        return shorts != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(shorts), 0) && shorts.length != 0;
    }

    public static boolean isNotEmpty(float[] floats) {
        return floats != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(floats), 0) && floats.length != 0;
    }

    public static boolean isNotEmpty(double[] doubles) {
        return doubles != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(doubles), 0) && doubles.length != 0;
    }

    public static boolean isNotEmpty(long[] longs) {
        return longs != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(longs), 0) && longs.length != 0;
    }

    public static boolean isNotEmpty(boolean[] booleans) {
        return booleans != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(booleans), false) && booleans.length != 0;
    }

    public static boolean isNotEmpty(char[] chars) {
        return chars != null && !ArrayHelper.isFullOf(ArrayHelper.wrap(chars), ' ') && chars.length != 0;
    }
}
