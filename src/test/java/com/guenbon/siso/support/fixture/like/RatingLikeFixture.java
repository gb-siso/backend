package com.guenbon.siso.support.fixture.like;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.like.RatingLike;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingLikeFixture {
    private Long id;
    protected Member member;
    private Rating rating;

    public static RatingLikeFixture builder() {
        return new RatingLikeFixture();
    }

    public RatingLikeFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public RatingLikeFixture setMember(Member member) {
        this.member = member;
        return this;
    }

    public RatingLikeFixture setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public RatingLike build() {
        return RatingLike.builder()
                .id(id)
                .member(member)
                .rating(rating)
                .build();
    }

    public static RatingLike of(Member member, Rating rating) {
        return RatingLikeFixture.builder()
                .setMember(member)
                .setRating(rating)
                .build();
    }
}
