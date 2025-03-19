package com.guenbon.siso.dto.congressman;


import com.guenbon.siso.dto.congressman.projection.CongressmanListProjectionDTO;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CongressmanGetListDTO {
    private Long id;
    private String name;
    private Double rate;
    private String timesElected;
    private String party;
    private String code;
    private String position;
    private String electoralDistrict;
    private String electoralType;
    private List<Integer> assemblySessions;
    private String sex;
    private String imageUrl;

    public static CongressmanGetListDTO from(CongressmanListProjectionDTO congressmanListProjectionDTO) {
        List<Integer> assemblySessionsList = null;
        if (congressmanListProjectionDTO.getAssemblySessions() != null) {
            assemblySessionsList = Arrays.stream(congressmanListProjectionDTO.getAssemblySessions().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return CongressmanGetListDTO.builder()
                .id(congressmanListProjectionDTO.getId())
                .name(congressmanListProjectionDTO.getName())
                .rate(congressmanListProjectionDTO.getRate())
                .timesElected(congressmanListProjectionDTO.getTimesElected())
                .party(congressmanListProjectionDTO.getParty())
                .code(congressmanListProjectionDTO.getCode())
                .position(congressmanListProjectionDTO.getPosition())
                .electoralDistrict(congressmanListProjectionDTO.getElectoralDistrict())
                .electoralType(congressmanListProjectionDTO.getElectoralType())
                .assemblySessions(assemblySessionsList)  // 변환된 List<Integer>
                .sex(congressmanListProjectionDTO.getSex())
                .imageUrl(congressmanListProjectionDTO.getImageUrl())
                .build();
    }
}
