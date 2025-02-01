package com.guenbon.siso.service;

import static com.guenbon.siso.exception.errorCode.CommonErrorCode.JSON_PARSE_ERROR;
import static com.guenbon.siso.support.constants.ApiConstants.AGE;
import static com.guenbon.siso.support.constants.ApiConstants.API_BILL_URL;
import static com.guenbon.siso.support.constants.ApiConstants.API_NEWS_URL;
import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;
import static com.guenbon.siso.support.constants.ApiConstants.CODE;
import static com.guenbon.siso.support.constants.ApiConstants.COMP_MAIN_TITLE;
import static com.guenbon.siso.support.constants.ApiConstants.HEAD;
import static com.guenbon.siso.support.constants.ApiConstants.KEY;
import static com.guenbon.siso.support.constants.ApiConstants.LIST_TOTAL_COUNT;
import static com.guenbon.siso.support.constants.ApiConstants.NEWS_API_PATH;
import static com.guenbon.siso.support.constants.ApiConstants.PROPOSER;
import static com.guenbon.siso.support.constants.ApiConstants.P_INDEX;
import static com.guenbon.siso.support.constants.ApiConstants.P_SIZE;
import static com.guenbon.siso.support.constants.ApiConstants.RESULT;
import static com.guenbon.siso.support.constants.ApiConstants.TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // API 키
    @Value("${api.news.key}")
    private String newsApikey;
    @Value("${api.bill.key}")
    private String billApikey;
    private final AESUtil aesUtil;
    private final CongressmanRepository congressmanRepository;

    public Congressman findById(final Long id) {
        return congressmanRepository.findById(id)
                .orElseThrow(() -> new CustomException(CongressmanErrorCode.NOT_EXISTS));
    }

    public CongressmanListDTO getCongressmanListDTO(final Pageable pageable, final String cursorId,
                                                    final Double cursorRate, final String party, final String search) {
        final long decryptedCursorId = aesUtil.decrypt(cursorId);
        final List<CongressmanGetListDTO> congressmanGetListDTOList = getCongressmanGetListDTOList(
                pageable, decryptedCursorId, cursorRate, party, search);

        final List<CongressmanDTO> congressmanDTOList = convertToCongressmanDTOList(congressmanGetListDTOList);

        return buildCongressmanListDTO(pageable, congressmanDTOList);
    }

    public NewsListDTO findNewsList(String encryptedCongressmanId, Pageable pageable) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final Congressman congressman = findById(congressmanId);

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(COMP_MAIN_TITLE, congressman.getName());

        final String response = getApiResponse(
                buildUrlString(pageable, API_NEWS_URL, newsApikey, paramMap));

        return parseResponse(response, pageable.getPageSize(), NEWS_API_PATH,
                (jsonNode, totalPage) -> NewsListDTO.of(jsonNode, totalPage));
    }

    public BillListDTO findBillList(String encryptedCongressmanId, Pageable pageable) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final Congressman congressman = findById(congressmanId);

        HashMap<String, String> params = new HashMap<>();
        params.put(AGE, "22");
        params.put(PROPOSER, congressman.getName() + "의원");

        final String response = getApiResponse(buildUrlString(pageable, API_BILL_URL, billApikey, params));
        return parseResponse(response, pageable.getPageSize(), BILL_API_PATH,
                (jsonNode, totalPage) -> BillListDTO.of(jsonNode, totalPage));
    }

    public <T> T parseResponse(final String response, final int pageSize, final String apiPath,
                               BiFunction<JsonNode, Integer, T> mapper) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode rootNode = parseJson(objectMapper, response);

        if (isApiResponseError(rootNode)) {
            handleApiError(rootNode);
        }

        final int totalCount = extractTotalCount(rootNode, apiPath);
        final int totalPage = calculateTotalPages(totalCount, pageSize);

        // mapper는 JsonNode와 Integer(totalPage)를 받아 특정 DTO로 변환하는 람다 함수
        return mapper.apply(rootNode.path(apiPath).get(1).path("row"), totalPage);
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
            throw new CustomException(CongressmanErrorCode.NOT_EXISTS);
        }
    }

    private static String getApiResponse(String uriString) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build()
                .get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String buildUrlString(Pageable pageable, String baseUrl, String apiKey, HashMap<String, String> params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(KEY, apiKey)
                .queryParam(TYPE, "json")
                .queryParam(P_INDEX, pageable.getPageNumber() + 1)
                .queryParam(P_SIZE, pageable.getPageSize());
        params.forEach(uriBuilder::queryParam);
        return uriBuilder.build(false).toUriString(); // 인코딩 비활성화
    }

    private JsonNode parseJson(final ObjectMapper objectMapper, final String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PARSE_ERROR);
        }
    }

    private boolean isApiResponseError(final JsonNode rootNode) {
        return rootNode.has(RESULT) && rootNode.path(RESULT).path(CODE).asText().contains("-");
    }

    private void handleApiError(final JsonNode rootNode) {
        final String errorCode = rootNode.path(RESULT).path(CODE).asText().split("-")[1];
        final CongressApiErrorCode congressApiErrorCode = CongressApiErrorCode.from(errorCode);
        throw new CustomException(congressApiErrorCode);
    }

    private int extractTotalCount(final JsonNode rootNode, String apiPath) {
        return rootNode.path(apiPath).get(0)
                .path(HEAD).get(0).path(LIST_TOTAL_COUNT).asInt();
    }

    private int calculateTotalPages(final int totalCount, final int pageSize) {
        return (totalCount + pageSize - 1) / pageSize; // 올림 계산
    }
}

