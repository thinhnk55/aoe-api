package vn.vietdefi.util.cryptographic;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.log.DebugLogger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
    private static SecretKeySpec secretKey(String secret) {
        SecretKeySpec secretKey = null;
        MessageDigest sha;
        try {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
        return secretKey;
    }

    public static String encrypt(String plainText, String secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey(secret));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
        return null;
    }

    public static String decrypt(String cipherText, String secret) {
        try {
            SecretKeySpec keySpec = secretKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.
                    doFinal(Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
        return null;
    }

    public static String encryptSHA1(String plain) {
       return DigestUtils.sha1Hex(plain);
    }
}