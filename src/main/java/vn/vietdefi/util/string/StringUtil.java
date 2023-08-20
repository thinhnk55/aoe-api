package vn.vietdefi.util.string;

import java.nio.charset.StandardCharsets;
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

    public static String sha512(String src) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(src.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashPassword = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hashPassword.append('0');
                hashPassword.append(hex);
            }
            return hashPassword.toString();
        } catch (Exception exception) {
            return null;
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

    public static String addThreeStarsToPhoneNumber(String phone) {
        StringBuilder sb = new StringBuilder(phone);
        int end = phone.length()-3;
        int start = end - 3;
        sb.replace(start, end, "***");
        return sb.toString();
    }
}
