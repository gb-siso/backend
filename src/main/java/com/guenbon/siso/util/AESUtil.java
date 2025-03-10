package com.guenbon.siso.util;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Component
public class AESUtil {
    private static final String ALGORITHM = "AES";
    private final String key; // 32바이트 고정 키

    public AESUtil(@Value("${aes.secret.key}") String secretKey) {
        this.key = secretKey;
    }

    public String encrypt(Long plainLong) {
        try {
            Objects.requireNonNull(plainLong);
            String target = String.valueOf(plainLong);
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            return encryptToBase64(target, cipher);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new CustomException(AESErrorCode.INVALID_INPUT);
        } catch (NullPointerException e) {
            throw new CustomException(AESErrorCode.NULL_VALUE);
        } catch (Exception e) {
            throw new CustomException(AESErrorCode.INTERNAL_SERVER);
        }
    }

    private static String encryptToBase64(String target, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] inputBytes = target.getBytes();
        byte[] resultBytes = cipher.doFinal(inputBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(resultBytes);
    }

    public Long decrypt(String encryptedText) {
        try {
            Objects.requireNonNull(encryptedText);
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            String decryptedString = decryptToString(cipher, encryptedText);
            return Long.parseLong(decryptedString);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new CustomException(AESErrorCode.INVALID_INPUT);
        } catch (NullPointerException e) {
            throw new CustomException(AESErrorCode.NULL_VALUE);
        } catch (Exception e) {
            throw new CustomException(AESErrorCode.INTERNAL_SERVER);
        }
    }

    private Cipher getCipher(int mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKeySpec);
        return cipher;
    }

    private String decryptToString(Cipher cipher, String encryptedText) throws Exception {
        byte[] inputBytes = Base64.getUrlDecoder().decode(encryptedText);
        byte[] resultBytes = cipher.doFinal(inputBytes);
        return new String(resultBytes, StandardCharsets.UTF_8);
    }
}
