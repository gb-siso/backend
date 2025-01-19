package com.guenbon.siso.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
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
        log.info("congressman name : {}", congressman.getName());

        // 외부 API 요청 URL 생성
        String apiUrl = "https://open.assembly.go.kr/portal/openapi/nauvppbxargkmyovh";
        String apiKey = "a7472423eb5c49d58f8c7eeae292b0db";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("Key", apiKey)
                .queryParam("Type", "json")
                .queryParam("pIndex", pageable.getPageNumber() + 1)
                .queryParam("pSize", pageable.getPageSize())
                .queryParam("COMP_MAIN_TITLE", congressman.getName());

        String uriString = uriBuilder.build(false).toUriString(); // 인코딩 비활성화

        log.info("reqeust uri : {}", uriString);

        String response = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build()
                .get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("응답 : {}", response);

        NewsListDTO newsListDTO = parseResponse(response, pageable.getPageSize());

        return newsListDTO;
    }

    public NewsListDTO parseResponse(String response, int pageSize) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            // 전체 기사 수
            int totalCount = rootNode.path("nauvppbxargkmyovh").get(0)
                    .path("head").get(0).path("list_total_count").asInt();
            System.out.println("전체 기사 수: " + totalCount);

            // 기사 리스트
            List<NewsDTO> newsDTOList = new ArrayList<>();
            int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

            JsonNode articles = rootNode.path("nauvppbxargkmyovh").get(1).path("row");
            for (JsonNode article : articles) {
                String rawTitle = article.path("COMP_MAIN_TITLE").asText();
                String title = StringEscapeUtils.unescapeHtml4(rawTitle); // HTML 엔티티 디코딩
                String link = article.path("LINK_URL").asText();
                String regDate = article.path("REG_DATE").asText(); // 등록 날짜 가져오기

                System.out.println("기사 제목: " + title);
                System.out.println("URL: " + link);
                System.out.println("날짜 : " + regDate);

                NewsDTO newsDTO = NewsDTO.of(title, link, regDate);
                newsDTOList.add(newsDTO);
                log.info("이 dto 추가 : {}", newsDTO);
            }
            return NewsListDTO.of(newsDTOList, totalPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
