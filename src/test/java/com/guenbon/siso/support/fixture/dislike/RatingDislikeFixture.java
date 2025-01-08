package com.guenbon.siso.support.fixture.dislike;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingDislikeFixture {
    private Long id;
    protected Member member;
    private Rating rating;

    public static RatingDislikeFixture builder() {
        return new RatingDislikeFixture();
    }

    public RatingDislikeFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public RatingDislikeFixture setMember(Member member) {
        this.member = member;
        return this;
    }

    public RatingDislikeFixture setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RatingDislike build() {
        return RatingDislike.builder()
                .id(id)
                .member(member)
                .rating(rating)
                .build();
    }

    public static RatingDislike of(Member member, Rating rating) {
        return RatingDislikeFixture.builder()
                .setMember(member)
                .setRating(rating)
                .build();
    }
}
