package com.guenbon.siso.support.util;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class AESUtilTest {

    @Test
    @DisplayName("id를 encrypt하면 암호화된 문자열을 얻는다")
    void encrypt_id_encryptedString() {
        // given
        final Long id  = 1L;
        // when
        final String encrypted = AESUtil.encrypt(id);
        // then
        log.info("encrypted : {}",encrypted);
        assertThat(encrypted).isNotEqualTo(id);
    }
}