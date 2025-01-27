package com.guenbon.siso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.ApiErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CongressmanService {

    public static final String API_NEWS_URL = "https://open.assembly.go.kr/portal/openapi/nauvppbxargkmyovh";
    public static final String NEWS_API_PATH = "nauvppbxargkmyovh";
    private static final String RESULT = "RESULT";
    private static final String CODE = "CODE";

    @Value("${api.news.key}")
    private String key;

    private final AESUtil aesUtil;

    private final CongressmanRepository congressmanRepository;

    private WebClient webClient = WebClient.builder().build();

    public Congressman findById(final Long id) {
        return congressmanRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(CongressmanErrorCode.NOT_EXISTS));
    }

    private List<CongressmanGetListDTO> getCongressmanGetListDTOList(Pageable pageable, Long cursorId,
                                                                     Double cursorRating, String party,
                                                                     String search) {
        return congressmanRepository.getList(pageable, cursorId, cursorRating, party, search);
    }

    private List<String> getRatedMemberImageList(final Long id) {
        ensureIdExists(id);
        return congressmanRepository.getRecentMemberImagesByCongressmanId(id);
    }

    public CongressmanListDTO getCongressmanListDTO(final Pageable pageable, final String cursorId,
                                                    final Double cursorRate, final String party, final String search) {
        final long decryptedCursorId = aesUtil.decrypt(cursorId);
        final List<CongressmanGetListDTO> congressmanGetListDTOList = getCongressmanGetListDTOList(
                pageable, decryptedCursorId, cursorRate, party, search);

        final List<CongressmanDTO> congressmanDTOList = convertToCongressmanDTOList(congressmanGetListDTOList);

        return buildCongressmanListDTO(pageable, congressmanDTOList);
    }

    private List<CongressmanDTO> convertToCongressmanDTOList(List<CongressmanGetListDTO> congressmanGetListDTOList) {
        return congressmanGetListDTOList.stream()
                .map(congressmanGetListDTO -> CongressmanDTO.of(
                        aesUtil.encrypt(congressmanGetListDTO.getId()),
                        congressmanGetListDTO,
                        getRatedMemberImageList(congressmanGetListDTO.getId())))
                .toList();
    }

    private CongressmanListDTO buildCongressmanListDTO(Pageable pageable, List<CongressmanDTO> congressmanDTOList) {
        final int pageSize = pageable.getPageSize();
        final CongressmanListDTO congressmanListDTO = CongressmanListDTO.builder()
                .congressmanList(congressmanDTOList)
                .build();

        if (congressmanDTOList.size() <= pageSize) {
            congressmanListDTO.setLastPage(true);
        } else {
            final CongressmanDTO lastElement = congressmanDTOList.get(pageSize);
            congressmanListDTO.setIdCursor(lastElement.getId());
            congressmanListDTO.setRateCursor(lastElement.getRate());
            congressmanListDTO.setLastPage(false);
        }

        return congressmanListDTO;
    }


    private void ensureIdExists(final Long id) {
        if (!congressmanRepository.existsById(id)) {
            throw new InternalServerException(CongressmanErrorCode.NOT_EXISTS);
        }
    }

    public NewsListDTO findNewsList(String encryptedCongressmanId, Pageable pageable) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final Congressman congressman = findById(congressmanId);
        final String response = getApiResponse(buildUrlString(pageable, congressman));
        return parseResponse(response, pageable.getPageSize());
    }

    private static String getApiResponse(String uriString) {
        return WebClient.builder()
                .baseUrl(API_NEWS_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build()
                .get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String buildUrlString(Pageable pageable, Congressman congressman) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_NEWS_URL)
                .queryParam("Key", key)
                .queryParam("Type", "json")
                .queryParam("pIndex", pageable.getPageNumber() + 1)
                .queryParam("pSize", pageable.getPageSize())
                .queryParam("COMP_MAIN_TITLE", congressman.getName());
        return uriBuilder.build(false).toUriString(); // 인코딩 비활성화
    }

    public NewsListDTO parseResponse(final String response, final int pageSize) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode rootNode = parseJson(objectMapper, response);

        if (isApiResponseError(rootNode)) {
            handleApiError(rootNode);
        }

        final int totalCount = extractTotalCount(rootNode);
        final int totalPage = calculateTotalPages(totalCount, pageSize);
        final List<NewsDTO> newsDTOList = extractArticles(rootNode);

        return NewsListDTO.of(newsDTOList, totalPage);
    }

    private JsonNode parseJson(final ObjectMapper objectMapper, final String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("뉴스 목록 api 요청 응답 파싱 예외 발생", e);
        }
    }

    private boolean isApiResponseError(final JsonNode rootNode) {
        return rootNode.has(RESULT) && rootNode.path(RESULT).path(CODE).asText().contains("-");
    }

    private void handleApiError(final JsonNode rootNode) {
        final String errorCode = rootNode.path(RESULT).path(CODE).asText().split("-")[1];
        final ApiErrorCode apiErrorCode = ApiErrorCode.from(errorCode);
        throw new ApiException(apiErrorCode);
    }

    private int extractTotalCount(final JsonNode rootNode) {
        return rootNode.path(NEWS_API_PATH).get(0)
                .path("head").get(0).path("list_total_count").asInt();
    }

    private int calculateTotalPages(final int totalCount, final int pageSize) {
        return (totalCount + pageSize - 1) / pageSize; // 올림 계산
    }

    private List<NewsDTO> extractArticles(final JsonNode rootNode) {
        final List<NewsDTO> newsDTOList = new ArrayList<>();
        final JsonNode articles = rootNode.path(NEWS_API_PATH).get(1).path("row");

        for (final JsonNode article : articles) {
            final String rawTitle = article.path("COMP_MAIN_TITLE").asText();
            final String title = StringEscapeUtils.unescapeHtml4(rawTitle);
            final String link = article.path("LINK_URL").asText();
            final String regDate = article.path("REG_DATE").asText();

            final NewsDTO newsDTO = NewsDTO.of(title, link, regDate);
            newsDTOList.add(newsDTO);
        }

        return newsDTOList;
    }

    public void findBillList(String encryptedCongressmanId, Pageable pageable) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final Congressman congressman = findById(congressmanId);
    }
}
