package com.guenbon.siso.dto.news;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import static com.guenbon.siso.support.constants.ApiConstants.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class NewsListDTO {

    private List<NewsDTO> newsList;
    private int lastPage;

    public static NewsListDTO of(List<NewsDTO> newsList, int lastPage) {
        return new NewsListDTO(newsList, lastPage);
    }

    public static NewsListDTO of(JsonNode articles, int lastPage) {
        final List<NewsDTO> newsDTOList = new ArrayList<>();

        for (final JsonNode article : articles) {
            final String rawTitle = article.path(COMP_MAN_TITLE).asText();
            final String title = StringEscapeUtils.unescapeHtml4(rawTitle);
            final String link = article.path(LINK_URL).asText();
            final String regDate = article.path(REG_DATE).asText();
            log.info("title : {}, link : {}, regDate : {}", title, link, regDate);

            final NewsDTO newsDTO = NewsDTO.of(title, link, regDate);
            newsDTOList.add(newsDTO);
        }
        return NewsListDTO.of(newsDTOList, lastPage);
    }
}

