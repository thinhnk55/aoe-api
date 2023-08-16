package vn.vietdefi.util.string;

import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

public class StringUtil {
    public static String sha256(String src) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(src.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static final Random random = new Random();
    private static final String charset = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generateRandomStringNumberCharacter(int length) {
        char[] buf = new char[length];
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charset.length());
            buf[i] = charset.charAt(index);
        }
        return new String(buf);
    }

    private static String randomString(char from, char to, int length) {
        int range = to - from;
        char[] buf = new char[length];
        for (int i = 0; i < buf.length; i++) {
            int index = random.nextInt(range);
            buf[i] = (char) (from + index);
        }
        return new String(buf);
    }

    public static String randomString(int length) {
        return randomString('a', 'z', length);
    }

    public static String randomStringUpper(int length) {
        return randomString('A', 'Z', length);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
