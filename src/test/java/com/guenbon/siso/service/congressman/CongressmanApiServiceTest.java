package com.guenbon.siso.service.congressman;

import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
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

import java.util.List;
import java.util.stream.Stream;

import static com.guenbon.siso.exception.errorCode.CongressApiErrorCode.MAX_REQUEST_LIMIT_EXCEEDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CongressmanApiServiceTest {

    @Autowired
    CongressmanApiService congressmanApiService;

    @MockitoBean(name = "congressmanService")
    CongressmanService congressmanService;

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

    @DisplayName("findNewsList 메서드에 유효한 파라미터를 전달하면 congressmanName이 제목에 포함된 NewsDTO가 작성날짜 내림차순으로 구성된 NewsListDTO를 반환한다")
    @Test
    void findNewsList_validParameters_NewsList() {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final Long decryptedCongressmanId = 1L;
        final Congressman congressman = CongressmanFixture.builder().setId(decryptedCongressmanId).build();
        final PageRequest pageable = PageRequest.of(0, 2);

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

        when(congressmanService.getCongressman(encryptedCongressmanId)).thenReturn(congressman);

        assertThatThrownBy(() -> congressmanApiService.findNewsList(encryptedCongressmanId, pageable))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(congressApiErrorCode.getMessage());
    }

    public static Stream<Arguments> provideApiExceptionParameters() {
        return Stream.of(
                Arguments.of(Named.named("1000을 넘는 pagesize", PageRequest.of(0, 10000)),
                        MAX_REQUEST_LIMIT_EXCEEDED),
                Arguments.of(Named.named("마지막 페이지를 넘는 pagenumber", PageRequest.of(9999999, 2)),
                        CongressApiErrorCode.NO_DATA_FOUND)
        );
    }
}