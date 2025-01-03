package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void getCongressmanListDTO_validParameters_CongressmanListDTO() {
        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = "adjfla123123adjklf";

        final CongressmanGetListDTO 살인자 = CongressmanGetListDTOFixture.builder()
                .setId(1L)
                .setRate(1.0).build();
        final CongressmanGetListDTO 성추행 = CongressmanGetListDTOFixture.builder()
                .setId(2L)
                .setRate(2.0).build();
        final CongressmanGetListDTO 전과없음 = CongressmanGetListDTOFixture.builder()
                .setId(3L)
                .setRate(3.0).build();

        final String encrypted_살인자_id = "dklqkdmsl123jak";
        final String encrypted_성추행_id = "dkladokdmsl123jak";
        final String encrypted_전과없음_id = "dklq18dla03jak";

        final List<CongressmanGetListDTO> congressmanGetListDTOList = List.of(전과없음, 성추행, 살인자);

        final List<String> 살인자_이미지 = List.of("image1", "image2", "image3", "image4");
        final List<String> 성추행_이미지 = Collections.emptyList();
        final List<String> 전과없음_이미지 = List.of("image5", "image6");

        final CongressmanDTO 살인자_dto = CongressmanDTO.builder()
                .id(encrypted_살인자_id)
                .name(살인자.getName())
                .rate(살인자.getRate())
                .party(살인자.getParty())
                .timesElected(살인자.getTimesElected())
                .ratedMemberImages(살인자_이미지)
                .build();
        final CongressmanDTO 성추행_dto = CongressmanDTO.builder()
                .id(encrypted_성추행_id)
                .name(성추행.getName())
                .rate(성추행.getRate())
                .party(성추행.getParty())
                .timesElected(성추행.getTimesElected())
                .ratedMemberImages(성추행_이미지)
                .build();
        final CongressmanDTO 전과없음_dto = CongressmanDTO.builder()
                .id(encrypted_전과없음_id)
                .name(전과없음.getName())
                .rate(전과없음.getRate())
                .party(전과없음.getParty())
                .timesElected(전과없음.getTimesElected())
                .ratedMemberImages(전과없음_이미지)
                .build();
        final List<CongressmanDTO> congressmanDTOListExpected = List.of(전과없음_dto, 성추행_dto, 살인자_dto);

        when(congressmanRepository.existsById(살인자.getId())).thenReturn(true);
        when(congressmanRepository.existsById(성추행.getId())).thenReturn(true);
        when(congressmanRepository.existsById(전과없음.getId())).thenReturn(true);

        when(aesUtil.decrypt(encryptedLongMax)).thenReturn(Long.MAX_VALUE);
        when(congressmanRepository.getList(pageable, Long.MAX_VALUE, null, null, null)).thenReturn(
                congressmanGetListDTOList);

        when(congressmanRepository.getRecentMemberImagesByCongressmanId(살인자.getId())).thenReturn(Optional.of(살인자_이미지));
        when(congressmanRepository.getRecentMemberImagesByCongressmanId(성추행.getId())).thenReturn(Optional.of(성추행_이미지));
        when(congressmanRepository.getRecentMemberImagesByCongressmanId(전과없음.getId())).thenReturn(
                Optional.of(전과없음_이미지));

        when(aesUtil.encrypt(살인자.getId())).thenReturn(encrypted_살인자_id);
        when(aesUtil.encrypt(성추행.getId())).thenReturn(encrypted_성추행_id);
        when(aesUtil.encrypt(전과없음.getId())).thenReturn(encrypted_전과없음_id);

        // when
        CongressmanListDTO congressmanListDTO = congressmanService.getCongressmanListDTO(pageable, encryptedLongMax,
                null, null, null);

        assertAll(
                () -> assertThat(congressmanListDTO.getLastPage()).isFalse(),
                () -> assertThat(congressmanListDTO.getIdCursor()).isEqualTo(encrypted_살인자_id),
                () -> assertThat(congressmanListDTO.getRateCursor()).isEqualTo(살인자.getRate()),
                () -> assertThat(congressmanListDTO.getCongressmanList()).usingRecursiveComparison()
                        .isEqualTo(congressmanDTOListExpected)
        );
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