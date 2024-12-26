package com.guenbon.siso.service;

import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
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

    public String encrypt(Long plainLong) {
        return process(plainLong, Cipher.ENCRYPT_MODE); // Long 값을 String으로 변환하여 encrypt 메서드 호출
    }

    public String decrypt(String encryptedText) {
        return process(encryptedText, Cipher.DECRYPT_MODE);
    }

    // 암호화 및 복호화를 공통 메서드로 추상화
    private String process(Object input, int mode) {
        try {
            String target = convertInputToString(input);
            Cipher cipher = getCipher(mode);
            byte[] inputBytes = mode == Cipher.ENCRYPT_MODE ? target.getBytes() : Base64.getDecoder().decode(target);
            byte[] resultBytes = cipher.doFinal(inputBytes);
            return mode == Cipher.ENCRYPT_MODE
                    ? Base64.getEncoder().encodeToString(resultBytes)
                    : new String(resultBytes);
        } catch (IllegalArgumentException | NullPointerException | ClassCastException e) {
            throw new BadRequestException(AESErrorCode.INVALID_INPUT);
        } catch (Exception e) {
            throw new InternalServerException(AESErrorCode.INTERNAL_SEVER);
        }
    }

    private String convertInputToString(Object input) {
        if (input instanceof Long) {
            return String.valueOf(input); // Long을 String으로 변환
        }
        return (String) input; // String 그대로 반환
    }

    private Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKeySpec);
        return cipher;
    }
}

