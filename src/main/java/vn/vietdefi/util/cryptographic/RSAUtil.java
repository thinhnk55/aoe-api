package vn.vietdefi.util.cryptographic;

import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.file.FileUtil;
import vn.vietdefi.util.log.DebugLogger;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {
    public static void generateKeyToFile(String privateKeyFile, String publicKeyFile) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            String publickeyStr = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            FileUtil.writeStringToFile(publicKeyFile, publickeyStr);
            String privateKeyStr = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            FileUtil.writeStringToFile(privateKeyFile, privateKeyStr);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    public static String encrypt(String plainText, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(bytes));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(bytes);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    public static PublicKey getPublicKey(String publickeyStr) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                    Base64.getDecoder().decode(
                            publickeyStr.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) {
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(
                            privateKeyStr.getBytes()));
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
        return privateKey;
    }
}
