package com.guenbon.siso.dto.congressman.common;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CongressmanDTOTest {

    @DisplayName("of 메서드에 유효하지 않은 null 형태 파라미터에 대해 InternalServerException을 던지며 에러코드는 CommonErrorCode이다")
    @ParameterizedTest
    @MethodSource("provideOfNullParameters")
    void of_nullParameters_InternalServerException(
            final String congressmanId,
            final CongressmanGetListDTO congressmanGetListDTO,
            final List<String> memberImages) {
        // given : parameters
        // when, then
        assertThrows(InternalServerException.class,
                () -> CongressmanDto.of(congressmanId, congressmanGetListDTO, memberImages),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    static Stream<Arguments> provideOfNullParameters() {
        Named<CongressmanGetListDTO> congressmanGetListDTO = Named.named("CongressmanGetListDTO",
                CongressmanGetListDTO.builder().build());
        Named<String> congressmanId = Named.named("암호화 된 congressmanId", "dkaghghk123dkaghghk123");
        Named<List<String>> imageUrls = Named.named("imageUrls", List.of("image1", "image2", "image3"));
        Named<Object> nullValue = Named.named("null value", null);

        return Stream.of(Arguments.of(
                        congressmanId,
                        nullValue,
                        imageUrls),
                Arguments.of(
                        nullValue,
                        congressmanGetListDTO,
                        imageUrls)
        );
    }
}