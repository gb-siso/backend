package com.guenbon.siso.dto.congressman.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "국회의원 목록 응답 dto")
public class CongressmanDTO {
    private String imageUrl;
    private String name;
    private String party;
    private Integer timesElected;
    private Float rating;
}
