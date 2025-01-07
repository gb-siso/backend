package com.guenbon.siso.dto.rating.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RatingListDTO {
    private List<RatingDetailDTO> ratingList;
    private String idCursor;

    public static RatingListDTO of(List<RatingDetailDTO> ratingDetailDTOList, String idCursor) {
        return new RatingListDTO(ratingDetailDTOList, idCursor);
    }
}
