package com.guenbon.siso.dto.congressman.common;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CongressmanDTOTest {

    @ParameterizedTest
    @MethodSource("provideOfNullParameters")
    void of_nullParameters_InternalServerException(final CongressmanGetListDTO congressmanGetListDTO,
                                                   final List<String> memberImages) {
        // given : parameters
        // when, then
        assertThrows(InternalServerException.class, () -> CongressmanDto.of(congressmanGetListDTO, memberImages),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    static Stream<Arguments> provideOfNullParameters() {
        return Stream.of(Arguments.of(null, List.of("image1", "image2", "image3")),
                Arguments.of(CongressmanGetListDTO.builder().build(), null)
        );
    }
}