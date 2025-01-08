package com.guenbon.siso.dto.rating.response;

import com.guenbon.siso.dto.cursor.count.CountCursor;
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
    private CountCursor countCursor;

    public static RatingListDTO of(List<RatingDetailDTO> ratingDetailDTOList, CountCursor countCursor) {
        return new RatingListDTO(ratingDetailDTOList, countCursor);
    }
}
