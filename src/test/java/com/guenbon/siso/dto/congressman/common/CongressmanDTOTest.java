package com.guenbon.siso.dto.congressman.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

class CongressmanDTOTest {
    @Test
    void of_validParameters_CongressmanDTO() {
        // given
        final String congressmanId = "dkaghghkehlsid123123dkaghghk";
        final CongressmanGetListDTO congressmanGetListDTO = CongressmanGetListDTO.builder()
                .name("리오넬 메시")
                .rate(5.0)
                .timesElected(10)
                .party("아르헨티나당")
                .build();
        final List<String> imageUrls = List.of("월드컵우승한사진.com", "발롱도르수상한사진.com", "바르셀로나챔스우승사진.com");

        final CongressmanDTO EXPECTED = CongressmanDTO.builder()
                .id(congressmanId)
                .name(congressmanGetListDTO.getName())
                .rate(congressmanGetListDTO.getRate())
                .party(congressmanGetListDTO.getParty())
                .timesElected(congressmanGetListDTO.getTimesElected())
                .ratedMemberImages(imageUrls)
                .build();
        // when
        final CongressmanDTO ACTUAL = CongressmanDTO.of(congressmanId, congressmanGetListDTO, imageUrls);

        // then
        assertThat(ACTUAL).usingRecursiveComparison().isEqualTo(EXPECTED);
    }

    @Test
    void of_validParametersNullImageUrl_CongressmanDTOEmptyListImageField() {
        // given
        final String congressmanId = "dkaghghkehlsid123123dkaghghk";
        final CongressmanGetListDTO congressmanGetListDTO = CongressmanGetListDTO.builder()
                .name("리오넬 메시")
                .rate(5.0)
                .timesElected(10)
                .party("아르헨티나당")
                .build();
        final List<String> imageUrls = null;

        final CongressmanDTO EXPECTED = CongressmanDTO.builder()
                .id(congressmanId)
                .name(congressmanGetListDTO.getName())
                .rate(congressmanGetListDTO.getRate())
                .party(congressmanGetListDTO.getParty())
                .timesElected(congressmanGetListDTO.getTimesElected())
                .ratedMemberImages(Collections.emptyList())
                .build();
        // when
        final CongressmanDTO ACTUAL = CongressmanDTO.of(congressmanId, congressmanGetListDTO, imageUrls);

        // then
        assertThat(ACTUAL).usingRecursiveComparison().isEqualTo(EXPECTED);
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