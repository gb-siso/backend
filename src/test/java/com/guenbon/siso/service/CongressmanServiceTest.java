package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.congressman.CongressmanGetListDTOFixture;
import com.guenbon.siso.util.AESUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;


@ExtendWith(MockitoExtension.class)
class CongressmanServiceTest {
    public static final String ENCRYPTED_LONG_MAX = "encryptedLongMax";
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
        final Congressman 이준석 = CongressmanFixture.builder().setId(1L).build();
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
        assertThatThrownBy(
                () -> congressmanService.findById(존재하지_않는_ID))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CongressmanErrorCode.NOT_EXISTS.getMessage());
    }

    @DisplayName("getCongressmanListDTO 메서드의 cursorId가 null이면 BadRequestException을 던지며 에러코드는 CommonErrorCode.NULL_VALUE_NOT_ALLOWED이다")
    @Test
    void getCongressmanListDTO_cursorIdNull_BadRequestException() {
        // given : parameters
        PageRequest pageable = PageRequest.of(0, 4);
        String cursorId = null; // cursorId가 null
        Double cursorRate = 4.5;
        String party = "민주당";
        String search = "김";

        // Mock 설정
        when(aesUtil.decrypt(null)).thenThrow(new CustomException(AESErrorCode.NULL_VALUE));

        // when, then
        assertThatThrownBy(
                () -> congressmanService.getCongressmanListDTO(pageable, cursorId, cursorRate, party, search))
                .isInstanceOf(CustomException.class)
                .hasMessage(AESErrorCode.NULL_VALUE.getMessage());
    }

    @DisplayName("getCongressmanListDTO가 마지막 스크롤이 아닌 경우 CongressmanListDTO를 반환한다")
    @Test
    void getCongressmanListDTO_notLastScroll_CongressmanListDTO() {
        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = ENCRYPTED_LONG_MAX;

        final List<CongressmanGetListDTO> congressmanGetListDTOList = List.of(
                CongressmanGetListDTOFixture.builder().setId(3L).setRate(3.0).build(),
                CongressmanGetListDTOFixture.builder().setId(2L).setRate(2.0).build(),
                CongressmanGetListDTOFixture.builder().setId(1L).setRate(1.0).build());
        final int size = congressmanGetListDTOList.size();

        final List<String> encryptedIdList = List.of("dklq18dla03jak", "dkladokdmsl123jak", "dklqkdmsl123jak");
        final List<List<String>> recentRatedImagesList = List.of(
                List.of("image5", "image6"), Collections.emptyList(),
                List.of("image1", "image2", "image3", "image4"));

        List<CongressmanDTO> congressmanDTOListExpected = IntStream.range(0, size).mapToObj(
                i -> getCongressmanDTO(encryptedIdList.get(i), congressmanGetListDTOList.get(i),
                        recentRatedImagesList.get(i))).collect(
                Collectors.toList());

        when(congressmanRepository.existsById(any(Long.class))).thenReturn(true);
        when(aesUtil.decrypt(encryptedLongMax)).thenReturn(Long.MAX_VALUE);
        when(congressmanRepository.getList(pageable, Long.MAX_VALUE, null, null, null)).thenReturn(
                congressmanGetListDTOList);

        IntStream.range(0, size).forEach(i -> {
            final Long congressmanId = congressmanGetListDTOList.get(i).getId();
            when(congressmanRepository.getRecentMemberImagesByCongressmanId(congressmanId)).thenReturn(
                    recentRatedImagesList.get(i));
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

    @DisplayName("getCongressmanListDTO가 마지막 스크롤인 경우 lastPage값이 true인 CongressmanListDTO를 반환한다")
    @Test
    void getCongressmanListDTO_lastScroll_CongressmanListDTO() {

        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = ENCRYPTED_LONG_MAX;

        final List<CongressmanGetListDTO> congressmanGetListDTOList = List.of(
                CongressmanGetListDTOFixture.builder().setId(3L).setRate(3.0).build(),
                CongressmanGetListDTOFixture.builder().setId(2L).setRate(2.0).build());

        final List<String> encryptedIdList = List.of("encrypted1", "encrypted2", "encrypted3");
        final int size = congressmanGetListDTOList.size();
        final List<List<String>> recentRatedImagesList = List.of(List.of("image1", "image2"), Collections.emptyList());

        List<CongressmanDTO> congressmanDTOListExpected = IntStream.range(0, size).mapToObj(
                i -> getCongressmanDTO(encryptedIdList.get(i), congressmanGetListDTOList.get(i),
                        recentRatedImagesList.get(i))).collect(Collectors.toList());

        when(congressmanRepository.existsById(any(Long.class))).thenReturn(true);
        when(aesUtil.decrypt(encryptedLongMax)).thenReturn(Long.MAX_VALUE);
        when(congressmanRepository.getList(pageable, Long.MAX_VALUE, null, null, null)).thenReturn(
                congressmanGetListDTOList);

        IntStream.range(0, size).forEach(i -> {
            final Long congressmanId = congressmanGetListDTOList.get(i).getId();
            when(congressmanRepository.getRecentMemberImagesByCongressmanId(congressmanId)).thenReturn(
                    recentRatedImagesList.get(i));
            when(aesUtil.encrypt(congressmanId)).thenReturn(encryptedIdList.get(i));
        });

        // when
        CongressmanListDTO congressmanListDTO = congressmanService.getCongressmanListDTO(pageable, encryptedLongMax,
                null, null, null);

        assertAll(
                () -> assertThat(congressmanListDTO.getLastPage()).isTrue(),
                () -> assertThat(congressmanListDTO.getIdCursor()).isNull(),
                () -> assertThat(congressmanListDTO.getRateCursor()).isNull(),
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
}