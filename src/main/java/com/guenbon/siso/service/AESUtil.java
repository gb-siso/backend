package com.guenbon.siso.service;

import com.guenbon.siso.exception.CustomException;
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
        try {
            if (plainLong == null) {
                throw new CustomException(AESErrorCode.NULL_VALUE);
            }
            String target = String.valueOf(plainLong); // Long을 String으로 변환
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] inputBytes = target.getBytes();
            byte[] resultBytes = cipher.doFinal(inputBytes);

            // Base64 URL-safe로 인코딩
            return Base64.getUrlEncoder().withoutPadding().encodeToString(resultBytes); // URL-safe 인코딩
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new CustomException(AESErrorCode.INVALID_INPUT);
        } catch (NullPointerException e) {
            throw new CustomException(AESErrorCode.NULL_VALUE);
        } catch (Exception e) {
            throw new CustomException(AESErrorCode.INTERNAL_SERVER);
        }
    }

    public Long decrypt(String encryptedText) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            // Base64 URL-safe로 디코딩
            byte[] inputBytes = Base64.getUrlDecoder().decode(encryptedText); // URL-safe 디코딩
            byte[] resultBytes = cipher.doFinal(inputBytes);
            String resultString = new String(resultBytes); // 복호화된 결과를 String으로 변환
            return Long.valueOf(resultString); // String을 Long으로 변환
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
}
