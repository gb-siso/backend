package com.guenbon.siso.dto.news;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewsDTO {
    private String title;
    private String link;
    private LocalDateTime regDate;

    public static NewsDTO of(String title, String link, String regDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // String -> LocalDateTime 변환
        LocalDateTime localDateTime = LocalDateTime.parse(regDate, formatter);
        return new NewsDTO(title, link, localDateTime);
    }

    @Override
    public String toString() {
        return "NewsDTO{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", regDate=" + regDate +
                '}';
    }
}
