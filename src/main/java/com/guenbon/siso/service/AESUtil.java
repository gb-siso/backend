package com.guenbon.siso.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AESUtil {
    private static final String ALGORITHM = "AES";
    private final String key; // 32바이트 고정 키

    public AESUtil(@Value("${aes.secret.key}") String secretKey) {
        this.key = secretKey;
    }

    public String encrypt(String plainText) throws Exception {
        return process(plainText, Cipher.ENCRYPT_MODE);
    }

    public String encrypt(Long plainLong) throws Exception {
        return encrypt(String.valueOf(plainLong)); // Long 값을 String으로 변환하여 encrypt 메서드 호출
    }

    public String decrypt(String encryptedText) throws Exception {
        return process(encryptedText, Cipher.DECRYPT_MODE);
    }

    // 암호화 및 복호화를 공통 메서드로 추상화
    private String process(String input, int mode) throws Exception {
        Cipher cipher = getCipher(mode);
        byte[] inputBytes = mode == Cipher.ENCRYPT_MODE ? input.getBytes() : Base64.getDecoder().decode(input);
        byte[] resultBytes = cipher.doFinal(inputBytes);
        return mode == Cipher.ENCRYPT_MODE
                ? Base64.getEncoder().encodeToString(resultBytes)
                : new String(resultBytes);
    }

    private Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKeySpec);
        return cipher;
    }
}

