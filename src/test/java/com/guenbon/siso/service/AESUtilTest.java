package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class AESUtilTest {

    private final String KEY = "jidamjidamjidamjidamjidamjidamji";
    final AESUtil aesUtil = new AESUtil(KEY);

    @Test
    @DisplayName("id를 암호화하고 복호화하면 원래 id를 얻는다")
    void encryptAndDecrypt_id_encryptedAndDecrypted() throws Exception {
        // given
        final Long id = 1L;

        // when
        final String encrypted = aesUtil.encrypt(id); // 암호화 수행
        final Long decrypted = aesUtil.decrypt(encrypted); // 복호화 수행

        // then
        assertThat(encrypted).isNotEqualTo(id); // 암호화된 값이 원래 값과 다른지 확인
        assertThat(decrypted).isEqualTo(id);   // 복호화된 값이 원래 값과 같은지 확인
    }

    @Test
    @DisplayName("encrypt와 decrypt에서 클라이언트 입력값에 의한 예외 발생 시 BadRequestException을 던진다")
    void encryptAndDecrypt_null_BadRequestException() {
        // encrypt null 값 예외 확인
        assertThrows(CustomException.class,
                () -> aesUtil.encrypt(null), AESErrorCode.NULL_VALUE.getMessage());

        // decrypt null 값 예외 확인
        assertThrows(CustomException.class,
                () -> aesUtil.decrypt(null), AESErrorCode.NULL_VALUE.getMessage());
    }
}