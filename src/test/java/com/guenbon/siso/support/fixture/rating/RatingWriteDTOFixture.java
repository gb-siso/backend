package com.guenbon.siso.support.fixture.rating;

import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingWriteDTOFixture {

    private String congressmanId = "congressmanId";
    private String content = "content";
    private Double rating = 3.0;

    public static RatingWriteDTOFixture builder() {
        return new RatingWriteDTOFixture();
    }

    public RatingWriteDTOFixture setCongressmanId(String congressmanId) {
        this.congressmanId = congressmanId;
        return this;
    }

    public RatingWriteDTOFixture setContent(String content) {
        this.content = content;
        return this;
    }

    public RatingWriteDTOFixture setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public RatingWriteDTO build() {
        return RatingWriteDTO.builder()
                .congressmanId(congressmanId)
                .content(content)
                .rating(rating)
                .build();
    }
}
