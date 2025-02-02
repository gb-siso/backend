package com.guenbon.siso.service.congressman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.bill.BillDTO;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.util.AESUtil;
import java.util.List;
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
class CongressmanApiServiceTest {

    @Autowired
    CongressmanApiService congressmanApiService;
    @MockitoBean
    CongressmanService congressmanService;
    @MockitoBean
    CongressmanRepository congressmanRepository;
    @MockitoBean
    AESUtil aesUtil;

    @DisplayName("findNewsList에 복호화에 실패하는 congressmanId를 전달하면 AESErrorCode.INVALID_INPUT 에러코드인 BadRequestException을 던진다")
    @Test
    void findNewsList_invalidCongressmanId_AESFailure_BadRequestException() {
        // given : invalid congressmanId
        final String encryptedCongressmanId = "invalid";
        final ErrorCode errorCode = AESErrorCode.INVALID_INPUT;

        when(congressmanService.getCongressman(encryptedCongressmanId)).thenThrow(new CustomException(errorCode));
        // when, then
        assertThrows(CustomException.class,
                () -> congressmanApiService.findNewsList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
    }

    @DisplayName("findNewsList에 존재하지 않는 congressmanId를 전달하면 CongressmanErrorCode.NOT_EXISTS 에러코드인 BadRequestException을 던진다")
    @Test
    void findNewsList_invalidCongressmanId_NotExist_BadRequestException() {
        // given : not existing congressmanId
        final String encryptedCongressmanId = "notExist";
        final ErrorCode errorCode = CongressmanErrorCode.NOT_EXISTS;

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(1L);
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenThrow(new CustomException(errorCode));

        // when, then
        assertThrows(CustomException.class,
                () -> congressmanApiService.findNewsList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
    }

    @DisplayName("findNewsList 메서드에 유효한 파라미터를 전달하면 congressmanName이 제목에 포함된 NewsDTO가 작성날짜 내림차순으로 구성된 NewsListDTO를 반환한다")
    @Test
    void findNewsList_validParameters_NewsList() {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();
        final PageRequest pageable = PageRequest.of(0, 2);

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenReturn(congressman);

        // when
        final NewsListDTO newsListDTO = congressmanApiService.findNewsList(encryptedCongressmanId, pageable);
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

    @DisplayName("findNewsList에 유효하지 않은 페이지 파라미터를 전달하면 ApiException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideApiExceptionParameters")
    void findNewsList_noDataParameters_ApiException(Pageable pageable, CongressApiErrorCode congressApiErrorCode) {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
//        when(congressmanRepository.findById(decryptedCongressmanId)).thenReturn(Optional.of(congressman));
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenReturn(congressman);

        assertThatThrownBy(() -> congressmanApiService.findNewsList(encryptedCongressmanId, pageable))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(congressApiErrorCode.getMessage());
    }

    public static Stream<Arguments> provideApiExceptionParameters() {
        return Stream.of(
                Arguments.of(Named.named("1000을 넘는 pagesize", PageRequest.of(0, 10000)),
                        CongressApiErrorCode.MAX_REQUEST_LIMIT_EXCEEDED),
                Arguments.of(Named.named("마지막 페이지를 넘는 pagenumber", PageRequest.of(9999999, 2)),
                        CongressApiErrorCode.NO_DATA_FOUND)
        );
    }

    @DisplayName("findBillList에 복호화에 실패하는 congressmanId를 전달하면 AESErrorCode.INVALID_INPUT 에러코드인 BadRequestException을 던진다")
    @Test
    void findBillList_invalidCongressmanId_AESFailure_BadRequestException() {
        // given : invalid congressmanId
        final String encryptedCongressmanId = "invalid";
        final ErrorCode errorCode = AESErrorCode.INVALID_INPUT;

        when(congressmanService.getCongressman(encryptedCongressmanId)).thenThrow(new CustomException(errorCode));
        // when, then
        assertThrows(CustomException.class,
                () -> congressmanApiService.findBillList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
    }

    @DisplayName("findBillList에 존재하지 않는 congressmanId를 전달하면 CongressmanErrorCode.NOT_EXISTS 에러코드인 BadRequestException을 던진다")
    @Test
    void findBillList_invalidCongressmanId_NotExist_BadRequestException() {
        // given : not existing congressmanId
        final String encryptedCongressmanId = "notExist";
        final ErrorCode errorCode = CongressmanErrorCode.NOT_EXISTS;

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(1L);
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenThrow(new CustomException(errorCode));

        // when, then
        assertThrows(CustomException.class,
                () -> congressmanApiService.findBillList(encryptedCongressmanId, PageRequest.of(0, 4)),
                errorCode.getMessage());
        verify(congressmanService).getCongressman(encryptedCongressmanId);
    }

    @DisplayName("findBillList에 유효하지 않은 페이지 파라미터를 전달하면 ApiException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideApiExceptionParameters")
    void findBillList_noDataParameters_ApiException(Pageable pageable, CongressApiErrorCode congressApiErrorCode) {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
//        when(congressmanRepository.findById(decryptedCongressmanId)).thenReturn(Optional.of(congressman));;
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenReturn(congressman);

        assertThatThrownBy(() -> congressmanApiService.findBillList(encryptedCongressmanId, pageable))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(congressApiErrorCode.getMessage());
    }

    @DisplayName("findBillList 메서드에 유효한 파라미터를 전달하면 congressmanName이 발의자에 포함된 BillDTO가 제안날짜 내림차순으로 구성된 BillListDTO를 반환한다")
    @Test
    void findBillList_validParameters_BillList() {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();
        final PageRequest pageable = PageRequest.of(0, 2);

//        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(decryptedCongressmanId);
//        when(congressmanRepository.findById(decryptedCongressmanId)).thenReturn(Optional.of(congressman));
        when(congressmanService.getCongressman(encryptedCongressmanId)).thenReturn(congressman);

        // when
        final BillListDTO BillListDTO = congressmanApiService.findBillList(encryptedCongressmanId, pageable);
        final List<BillDTO> BillDTOList = BillListDTO.getBillList();

        // then
        // 검색어 이름 포함 검증
        final String congressmanName = congressman.getName();
        assertThat(BillDTOList).as("모든 발의안 제목에 의원 이름이 포함되어야 합니다.")
                .allSatisfy(BillDTO -> assertThat(
                        List.of(
                                BillDTO.getProposer(),
                                BillDTO.getPublProposer(),
                                BillDTO.getRstProposer()
                        )
                ).anyMatch(field -> field != null && (field.contains(congressmanName))));

        // regdate desc 정렬 검증
        final int BillCount = BillDTOList.size(); // 중복 제거
        for (int i = 0; i < BillCount - 1; i++) {
            assertThat(BillDTOList.get(i).getProposeDate())
                    .as("발의안은 proposeDate 기준 내림차순으로 정렬되어야 합니다.")
                    .isAfterOrEqualTo(BillDTOList.get(i + 1).getProposeDate());
        }
    }
}