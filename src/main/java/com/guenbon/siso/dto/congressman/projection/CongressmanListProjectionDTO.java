package com.guenbon.siso.dto.congressman.projection;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CongressmanListProjectionDTO {
    private Long id;
    private String name;
    private Double rate;
    private String timesElected;
    private String party;
    private String code;
    private String position;
    private String electoralDistrict;
    private String electoralType;
    // concat 해서 가져오는 형식이므로 String
    private String assemblySessions;
    private String sex;
    private String imageUrl;
}
