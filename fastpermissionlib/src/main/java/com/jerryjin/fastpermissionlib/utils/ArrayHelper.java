package com.jerryjin.fastpermissionlib.utils;

public class ArrayHelper {

    public static boolean ifContains(int[] integers, int value) {
        return ifContains(wrap(integers), value);
    }

    public static boolean ifContains(byte[] bytes, byte value) {
        return ifContains(wrap(bytes), value);
    }

    public static boolean ifContains(short[] shorts, short value) {
        return ifContains(wrap(shorts), value);
    }

    public static boolean ifContains(float[] floats, float value) {
        return ifContains(wrap(floats), value);
    }

    public static boolean ifContains(double[] doubles, double value) {
        return ifContains(wrap(doubles), value);
    }

    public static boolean ifContains(long[] longs, long value) {
        return ifContains(wrap(longs), value);
    }

    public static boolean ifContains(char[] chars, char value) {
        return ifContains(wrap(chars), value);
    }

    public static boolean ifContains(boolean[] booleans, boolean value) {
        return ifContains(wrap(booleans), value);
    }

    public static <T> boolean ifContains(T[] array, T t) {
        boolean result = false;
        for (T obj : array) {
            if (obj.equals(t)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static <T> boolean isFullOf(T[] ts, T value) {
        int counter = 0;
        if (ObjectHelper.isNotEmpty(ts)) {
            for (T t : ts) {
                if (t.equals(value)) {
                    counter++;
                }
            }
            return counter == ts.length;
        }
        return false;
    }

    public static Integer[] wrap(int[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Integer[] integers = new Integer[len];
            int index = 0;
            for (int i : values) {
                integers[index++] = i;
            }
            return integers;
        }
        return null;
    }

    public static Byte[] wrap(byte[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Byte[] bytes = new Byte[len];
            int index = 0;
            for (byte b : values) {
                bytes[index++] = b;
            }
            return bytes;
        }
        return null;
    }

    public static Short[] wrap(short[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Short[] shorts = new Short[len];
            int index = 0;
            for (short s : values) {
                shorts[index++] = s;
            }
            return shorts;
        }
        return null;
    }

    public static Float[] wrap(float[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Float[] floats = new Float[len];
            int index = 0;
            for (float f : values) {
                floats[index++] = f;
            }
            return floats;
        }
        return null;
    }

    public static Double[] wrap(double[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Double[] doubles = new Double[len];
            int index = 0;
            for (double d : values) {
                doubles[index++] = d;
            }
            return doubles;
        }
        return null;
    }

    public static Long[] wrap(long[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Long[] longs = new Long[len];
            int index = 0;
            for (long l : values) {
                longs[index++] = l;
            }
            return longs;
        }
        return null;
    }

    public static Boolean[] wrap(boolean[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Boolean[] booleans = new Boolean[len];
            int index = 0;
            for (boolean b : values) {
                booleans[index++] = b;
            }
            return booleans;
        }
        return null;
    }

    public static Character[] wrap(char[] values) {
        if (values != null && values.length != 0) {
            int len = values.length;
            Character[] characters = new Character[len];
            int index = 0;
            for (char c : values) {
                characters[index++] = c;
            }
            return characters;
        }
        return null;
    }
}
