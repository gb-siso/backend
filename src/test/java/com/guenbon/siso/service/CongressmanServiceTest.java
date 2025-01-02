package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.CongressmanFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
        final Congressman 이준석 = CongressmanFixture.builder()
                .setId(1L)
                .setName("이준석").build();
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

    @Test
    @DisplayName("getList 호출 시 Pageable이 null이면 BadRequestException을 던진다")
    void getList_pageRequestNull_BadRequestException() {
        assertThrows(BadRequestException.class,
                () -> congressmanService.getList(null, Long.MAX_VALUE, null, null, null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("getList 호출 시 cursorId가 null이면 BadRequestException을 던진다")
    void getList_cursorIdNull_BadRequestException() {
        final PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("rating").ascending());

        assertThrows(BadRequestException.class,
                () -> congressmanService.getList(pageRequest, null, null, null, null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("getList 호출 시 유효한 입력값이면 List<CongressmanGetListDTO> 을 반환한다")
    void getList_validInput_expectedList() {
        // given
        final Congressman 서재민 = CongressmanFixture.builder().setName("서재민").build();
        final Congressman 김선균 = CongressmanFixture.builder().setName("김선균").build();
        final Congressman 정승수 = CongressmanFixture.builder().setName("정승수").build();
        final PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("rating").descending());
        final List<CongressmanGetListDTO> expected = new ArrayList<>(
                List.of(toDTO(서재민, 5.0), toDTO(김선균, 4.0), toDTO(정승수, 3.0)));

        when(congressmanRepository.getList(pageRequest, Long.MAX_VALUE, null, null, null)).thenReturn(expected);

        // when
        final List<CongressmanGetListDTO> actual = congressmanService.getList(pageRequest, Long.MAX_VALUE, null, null,
                null);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("getRecentRatedMembersImages 파라미터로 존재하지 않는 국회의원 id를 전달하면 InternalServerException을 던지며 에러코드는 NOT_EXISTS 이다")
    void getRecentRatedMembersImages_notExistCongressmanId_InternalServerException() {
        // given
        final Long 존재하지_않는_국회의원_ID = 9182L;
        when(congressmanRepository.existsById(존재하지_않는_국회의원_ID)).thenReturn(false);

        // when, then
        assertThrows(InternalServerException.class,
                () -> congressmanService.getRecentRatedMembersImages(존재하지_않는_국회의원_ID),
                CongressmanErrorCode.NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("getRecentRatedMembersImages 유효한 국회의원 id를 전달하면 List<String> 형태의 이미지 경로 리스트를 반환한다")
    void getRecentRatedMembersImages_validCongressmanId_imageUrlList() {
        // given
        final Long VALID_ID = 13L;
        final Optional<List<String>> EXPECTED = Optional.of(List.of("image1", "image2", "image3"));
        when(congressmanRepository.existsById(VALID_ID)).thenReturn(true);
        when(congressmanRepository.getRecentMemberImagesByCongressmanId(VALID_ID)).thenReturn(EXPECTED);

        // when
        final Optional<List<String>> ACTUAL = congressmanService.getRecentRatedMembersImages(VALID_ID);

        // then
        assertThat(ACTUAL).usingRecursiveComparison().isEqualTo(EXPECTED);
    }

    @ParameterizedTest(name = "{0} 파라미터 전달")
    @MethodSource("provideInvalidCongressmanGetListDTO")
    @DisplayName("buildCongressmanDTOWithImages에 null 형태 파라미터 또는 값을 전달하면 InternalServerException를 던지며 CommonErrorCode.NULL_VALUE_NOT_ALLOWED 에러코드이다")
    void buildCongressmanDTOWithImages_invalidParameters_InternalServerException(
            final CongressmanGetListDTO INVALID_DTO) {
        // when, then
        assertThrows(InternalServerException.class, () -> congressmanService.buildCongressmanDTOWithImages(INVALID_DTO),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("buildCongressmanDTOWithImages가 유효한 파라미터에 대해 CongressmanDTO를 반환한다")
    void buildCongressmanDTOWithImages_validCongressmanGetListDTO_CongressmanDTO() {
        // given
        final CongressmanGetListDTO 이준석 = CongressmanGetListDTO.builder()
                .id(12L)
                .name("이준석")
                .rate(4.5)
                .party("국민의힘")
                .timesElected(2)
                .build();

        final String ENCRYPTED_ID = "dkaghghk123ehls123id1232";

        final CongressmanDTO EXPECTED = CongressmanDTO.builder()
                .id(ENCRYPTED_ID)
                .name(이준석.getName())
                .rating(이준석.getRate())
                .party(이준석.getParty())
                .timesElected(이준석.getTimesElected())
                .build();

        when(congressmanRepository.existsById(이준석.getId())).thenReturn(true);
        when(aesUtil.encrypt(이준석.getId())).thenReturn(ENCRYPTED_ID);

        // when
        final CongressmanDTO ACTUAL = congressmanService.buildCongressmanDTOWithImages(이준석);

        // then
        assertThat(ACTUAL).usingRecursiveComparison().isEqualTo(EXPECTED);
    }

    private static Stream<Arguments> provideInvalidCongressmanGetListDTO() {
        return Stream.of(
                Arguments.of(Named.named("null", null)),
                Arguments.of(Named.named("congressmanId가 null인 CongressmanGetListDTO", null))
        );
    }


    private CongressmanGetListDTO toDTO(Congressman congressman, double rate) {
        return CongressmanGetListDTO.builder()
                .id(congressman.getId())
                .name(congressman.getName())
                .rate(rate)
                .build();
    }
}