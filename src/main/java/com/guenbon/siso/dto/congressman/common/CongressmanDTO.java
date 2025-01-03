package com.guenbon.siso.dto.congressman.common;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "국회의원 목록 응답 dto")
public class CongressmanDTO {
    private String id;
    private String name;
    private String party;
    private Integer timesElected;
    private Double rate;
    @Schema(description = "해당 의원 최근에 평가한 회원 4명 이미지")
    private List<String> ratedMemberImages;

    public static CongressmanDTO of(String encryptedCongressmanId, CongressmanGetListDTO congressmanGetListDTO,
                                    List<String> memberImages) {
        if (encryptedCongressmanId == null || congressmanGetListDTO == null) {
            throw new InternalServerException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
        return null;
    }
}
