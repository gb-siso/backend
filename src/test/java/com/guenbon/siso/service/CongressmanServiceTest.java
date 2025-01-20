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
import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.exception.errorCode.ApiErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.congressman.CongressmanGetListDTOFixture;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@SpringBootTest
class CongressmanServiceTest {

    @Autowired
    CongressmanService congressmanService;
    @MockitoBean
    CongressmanRepository congressmanRepository;
    @MockitoBean
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
        when(aesUtil.decrypt(null)).thenThrow(new BadRequestException(AESErrorCode.NULL_VALUE));

        // when, then
        assertThrows(BadRequestException.class,
                () -> congressmanService.getCongressmanListDTO(pageable, cursorId, cursorRate, party, search),
                AESErrorCode.NULL_VALUE.getMessage());
    }

    @DisplayName("getCongressmanListDTO가 마지막 스크롤이 아닌 경우 CongressmanListDTO를 반환한다")
    @Test
    void getCongressmanListDTO_notLastScroll_CongressmanListDTO() {
        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = "adjfla123123adjklf";

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

    @DisplayName("getCongressmanListDTO가 마지막 스크롤인 경우 CongressmanListDTO를 반환한다")
    @Test
    void getCongressmanListDTO_lastScroll_CongressmanListDTO() {

        final PageRequest pageable = PageRequest.of(0, 2);
        final String encryptedLongMax = "adjfla123123adjklf";

        final List<CongressmanGetListDTO> congressmanGetListDTOList = List.of(
                CongressmanGetListDTOFixture.builder().setId(3L).setRate(3.0).build(),
                CongressmanGetListDTOFixture.builder().setId(2L).setRate(2.0).build());
        final int size = congressmanGetListDTOList.size();

        final List<String> encryptedIdList = List.of("dklq18dla03jak", "dkladokdmsl123jak", "dklqkdmsl123jak");
        final List<List<String>> recentRatedImagesList = List.of(
                List.of("image5", "image6"), Collections.emptyList());

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

    @DisplayName("findNewsList 메서드의 congressmanId가 AES 복호화에 실패하면 BadRequestException을 던진다")
    @Test
    void findNewsList_invalidCongressmanId_AESFailure_BadRequestException() {
        // given : invalid congressmanId
        final String encryptedCongressmanId = "invalid";
        final ErrorCode errorCode = AESErrorCode.INVALID_INPUT;

        when(aesUtil.decrypt(encryptedCongressmanId)).thenThrow(new BadRequestException(errorCode));
        // when, then
        assertThrows(BadRequestException.class,
                () -> congressmanService.findNewsList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
    }

    @DisplayName("findNewsList 메서드의 congressmanId가 존재하지 않는 값이면 BadRequestException을 던진다")
    @Test
    void findNewsList_invalidCongressmanId_NotExist_BadRequestException() {
        // given : not existing congressmanId
        final String encryptedCongressmanId = "notExist";
        final ErrorCode errorCode = CongressmanErrorCode.NOT_EXISTS;

        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(1L);
        when(congressmanRepository.findById(1L)).thenThrow(new BadRequestException(errorCode));

        // when, then
        assertThrows(BadRequestException.class,
                () -> congressmanService.findNewsList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
    }

    @DisplayName("findNewsList 메서드에 유효한 파라미터를 전달하면 NewsListDTO를 반환한다")
    @Test
    void findNewList_validParameters_NewsList() {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();
        final PageRequest pageable = PageRequest.of(0, 2);

        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
        when(congressmanRepository.findById(decryptedCongressmanId)).thenReturn(Optional.of(congressman));

        // when
        final NewsListDTO newsListDTO = congressmanService.findNewsList(encryptedCongressmanId, pageable);
        final List<NewsDTO> newsDTOList = newsListDTO.getNewsList();

        // then
        // 검색어 이름 포함 검증
        final String congressmanName = congressman.getName();
        assertThat(newsDTOList).as("모든 뉴스 제목에 의원 이름이 포함되어야 합니다.")
                .allSatisfy(newsDTO -> assertThat(newsDTO.getTitle()).contains(congressmanName));

        // regdate desc 정렬 검증
        final int newsCount = newsDTOList.size(); // 중복 제거
        for (int i = 0; i < newsCount - 1; i++) {
            assertThat(newsDTOList.get(i).getRegDate())
                    .as("뉴스는 regDate 기준 내림차순으로 정렬되어야 합니다.")
                    .isAfterOrEqualTo(newsDTOList.get(i + 1).getRegDate());
        }
    }

    @DisplayName("findNewsList에 마지막 페이지를 초과하는 파라미터를 전달하면 ApiException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideApiExceptionParameters")
    void findNewsList_noDataParameters_ApiException(Pageable pageable, ApiErrorCode apiErrorCode) {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();

        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
        when(congressmanRepository.findById(decryptedCongressmanId)).thenReturn(Optional.of(congressman));

        assertThatThrownBy(() -> congressmanService.findNewsList(encryptedCongressmanId, pageable))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(apiErrorCode.getMessage());
    }

    // TODO : 데이터가 없는 경우에 대해서는 빈 리스트로 반환해야 하는게 아닌지?
    public static Stream<Arguments> provideApiExceptionParameters() {
        return Stream.of(
                Arguments.of(Named.named("1000을 넘는 pagesize", PageRequest.of(0, 10000)),
                        ApiErrorCode.MAX_REQUEST_LIMIT_EXCEEDED),
                Arguments.of(Named.named("마지막 페이지를 넘는 pagenumber", PageRequest.of(9999999, 2)),
                        ApiErrorCode.NO_DATA_FOUND)
        );
    }
}