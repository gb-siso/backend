package com.guenbon.siso.dto.bill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BillDTO {
    private String title;
    // 제안자
    private String proposer;
    // 공동 발의자
    private String publProposer;
    // 대표 발의자
    private String rstProposer;
    private String link;
    private LocalDate proposeDate; // LocalDate로 변경

    public static BillDTO of(String title,
                             String proposer, String publProposer, String rstProposer,
                             String link, String proposeDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // String -> LocalDate 변환
        LocalDate localDate = LocalDate.parse(proposeDate, formatter);
        return new BillDTO(title, proposer, publProposer, rstProposer, link, localDate); // LocalDate 사용
    }

    @Override
    public String toString() {
        return "BillDTO{" +
                "title='" + title + '\'' +
                ", proposer='" + proposer + '\'' +
                ", link='" + link + '\'' +
                ", proposeDate=" + proposeDate +
                '}';
    }
}
