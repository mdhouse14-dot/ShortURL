package app.util;

public final class Base62 {
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private Base62() {}

    public static String encode(long value) {
        if (value == 0) return "0";
        StringBuilder sb = new StringBuilder();
        long v = value;
        while (v > 0) {
            int idx = (int) (v % 62);
            sb.append(ALPHABET[idx]);
            v /= 62;
        }
        return sb.reverse().toString();
    }
}