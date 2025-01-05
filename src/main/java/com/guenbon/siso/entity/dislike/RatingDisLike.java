package com.guenbon.siso.entity.dislike;

import com.guenbon.siso.entity.Rating;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDisLike extends DisLike {
    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;
}
