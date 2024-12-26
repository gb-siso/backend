package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;

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
        final String decrypted = aesUtil.decrypt(encrypted);
        // then
        log.info("decrypted : {}", decrypted);
        assertThat(Long.valueOf(decrypted)).isEqualTo(id);
    }
}