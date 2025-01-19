package com.guenbon.siso.dto.news;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewsListDTO {
    private List<NewsDTO> newsList;
    private int lastPage;

    public static NewsListDTO of(List<NewsDTO> newsList, int lastPage) {
        return new NewsListDTO(newsList, lastPage);
    }
}
