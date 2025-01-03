package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "국회의원 목록 응답 dto")
public class CongressmanListDTO {
    @Schema(description = "국회의원 목록")
    private List<CongressmanDTO> congressmanList;
    @Schema(description = "무한스크롤 커서값")
    private String idCursor;
    private Double rateCursor;
    @Schema(description = "무한스크롤 마지막 데이터 여부")
    private Boolean lastPage;
}
