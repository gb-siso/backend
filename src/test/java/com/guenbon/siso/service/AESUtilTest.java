package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class AESUtilTest {

    private final String KEY = "jidamjidamjidamjidamjidamjidamji";
    final AESUtil aesUtil = new AESUtil(KEY);

    @Test
    @DisplayName("id를 encrypt하면 암호화된 문자열을 얻는다")
    void encrypt_id_encryptedString() throws Exception {
        // given
        final Long id = 1L;
        // when
        final String encrypted = aesUtil.encrypt(id);
        // then
        log.info("encrypted : {}", encrypted);
        assertThat(encrypted).isNotEqualTo(id);
    }

    @Test
    @DisplayName("암호화된 문자열를 decrypt하면 id를 얻는다")
    void decrypt_encryptedString_id() throws Exception {
        // given
        final Long id = 1L;
        final String encrypted = aesUtil.encrypt(id);
        // when
        final Long decrypted = aesUtil.decrypt(encrypted);
        // then
        log.info("decrypted : {}", decrypted);
        assertThat(decrypted).isEqualTo(id);
    }

    @Test
    @DisplayName("encrypt에서 클라이언트 입력값에 의한 예외 발생 시 BadRequestException을 던진다")
    void encrypt_null_BadRequestException() {
        assertThrows(BadRequestException.class, () -> aesUtil.encrypt(null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
        assertThrows(BadRequestException.class, () -> aesUtil.decrypt(null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }
}