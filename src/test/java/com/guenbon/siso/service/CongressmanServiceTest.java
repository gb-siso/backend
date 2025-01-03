package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.CongressmanFixture;
import com.guenbon.siso.support.fixture.CongressmanGetListDTOFixture;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CongressmanServiceTest {

    @InjectMocks
    CongressmanService congressmanService;
    @Mock
    CongressmanRepository congressmanRepository;
    @Mock
    AESUtil aesUtil;

    @Test
    @DisplayName("findById가 존재하는 국회의원을 반환한다")
    void findById_exist_Congressman() {
        // given
        final Congressman 이준석 = CongressmanFixture.builder().setId(1L).setName("이준석").build();
        when(congressmanRepository.findById(이준석.getId())).thenReturn(Optional.of(이준석));

        // when
        final Congressman actual = congressmanService.findById(이준석.getId());

        // then
        assertThat(actual).isEqualTo(이준석);
    }

    @Test
    @DisplayName("findById가 존재하지 않는 국회의원에 대해 BadRequestException을 던진다")
    void findById_notExist_NotExistException() {
        // given
        final Long 존재하지_않는_ID = 1L;
        when(congressmanRepository.findById(존재하지_않는_ID)).thenReturn(Optional.empty());
        // when then
        assertThrows(BadRequestException.class, () -> congressmanService.findById(존재하지_않는_ID),
                CongressmanErrorCode.NOT_EXISTS.getMessage());
    }

    @DisplayName("getCongressmanListDTO 메서드의 특정 파라미터가 null이면 BadRequestException을 던지며 에러코드는 CommonErrorCode.NULL_VALUE_NOT_ALLOWED이다")
    @ParameterizedTest(name = "{0}일 경우")
    @MethodSource("provideGetCongressmanListDTONullParameters")
    void getCongressmanListDTO_nullParameters_BadRequestException(String description, Pageable pageable,
                                                                  String cursorId, Double cursorRate,
                                                                  String party, String search) {
        // given : parameters
        if (cursorId == null) {
            when(aesUtil.decrypt(null)).thenThrow(new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED));
        }
        // when, then
        assertThrows(BadRequestException.class,
                () -> congressmanService.getCongressmanListDTO(pageable, cursorId, cursorRate, party, search),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("getCongressmanListDTO가 유효한 파라미터에 대해 CongressmanListDTO를 반환한다")
    @Test
    void getCongressmanListDTO_validParameters_CongressmanListDTO() {
        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = "adjfla123123adjklf";

        final List<CongressmanGetListDTO> congressmanGetListDTOList = List.of(
                CongressmanGetListDTOFixture.builder().setId(3L).setRate(3.0).build(),
                CongressmanGetListDTOFixture.builder().setId(2L).setRate(2.0).build(),
                CongressmanGetListDTOFixture.builder().setId(1L).setRate(1.0).build());

        final List<String> encryptedIdList = List.of("dklq18dla03jak", "dkladokdmsl123jak", "dklqkdmsl123jak");
        final List<List<String>> recentRatedImagesList = List.of(
                List.of("image5", "image6"), Collections.emptyList(),
                List.of("image1", "image2", "image3", "image4"));

        List<CongressmanDTO> congressmanDTOListExpected = IntStream.range(0, 3).mapToObj(
                i -> getCongressmanDTO(encryptedIdList.get(i), congressmanGetListDTOList.get(i),
                        recentRatedImagesList.get(i))).collect(
                Collectors.toList());

        when(congressmanRepository.existsById(any(Long.class))).thenReturn(true);
        when(aesUtil.decrypt(encryptedLongMax)).thenReturn(Long.MAX_VALUE);
        when(congressmanRepository.getList(pageable, Long.MAX_VALUE, null, null, null)).thenReturn(
                congressmanGetListDTOList);

        IntStream.range(0, 3).forEach(i -> {
            final Long congressmanId = congressmanGetListDTOList.get(i).getId();
            when(congressmanRepository.getRecentMemberImagesByCongressmanId(congressmanId)).thenReturn(
                    Optional.of(recentRatedImagesList.get(i)));
            when(aesUtil.encrypt(congressmanId)).thenReturn(encryptedIdList.get(i));
        });

        // when
        CongressmanListDTO congressmanListDTO = congressmanService.getCongressmanListDTO(pageable, encryptedLongMax,
                null, null, null);

        final int pageSize = pageable.getPageSize();
        assertAll(
                () -> assertThat(congressmanListDTO.getLastPage()).isFalse(),
                () -> assertThat(congressmanListDTO.getIdCursor()).isEqualTo(encryptedIdList.get(pageSize)),
                () -> assertThat(congressmanListDTO.getRateCursor()).isEqualTo(
                        congressmanGetListDTOList.get(pageSize).getRate()),
                () -> assertThat(congressmanListDTO.getCongressmanList()).usingRecursiveComparison()
                        .isEqualTo(congressmanDTOListExpected)
        );
    }

    private static CongressmanDTO getCongressmanDTO(String encryptedId, CongressmanGetListDTO congressmanGetListDTO,
                                                    List<String> ratedMemberImages) {
        return CongressmanDTO.builder()
                .id(encryptedId)
                .name(congressmanGetListDTO.getName())
                .rate(congressmanGetListDTO.getRate())
                .party(congressmanGetListDTO.getParty())
                .timesElected(congressmanGetListDTO.getTimesElected())
                .ratedMemberImages(ratedMemberImages)
                .build();
    }

    public static Stream<Arguments> provideGetCongressmanListDTONullParameters() {

        PageRequest pageable = PageRequest.of(0, 4);
        String cursorId = "alkdfjad456asdf456123";
        Double cursorRate = 4.5;
        String party = "민주당";
        String search = "김";

        return Stream.of(
                Arguments.of("pageable = null", null, cursorId, cursorRate, party, search),
                Arguments.of("cursorId = null", pageable, null, cursorRate, party, search)
        );
    }
}