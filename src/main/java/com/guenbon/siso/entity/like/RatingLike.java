package com.guenbon.siso.entity.like;

import com.guenbon.siso.entity.Rating;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingLike extends Like {
    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;
}
