package com.guenbon.siso.entity.like;

import com.guenbon.siso.entity.Rating;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RatingLike extends Like {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    public RatingLike setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public String toString() {
        return "RatingLike{" +
                "rating=" + rating +
                ", member=" + member +
                '}';
    }
}
