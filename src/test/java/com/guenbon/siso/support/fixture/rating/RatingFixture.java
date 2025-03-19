package com.guenbon.siso.support.fixture.rating;


import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.entity.like.RatingLike;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingFixture {

    private Long id;

    private Member member;

    private Congressman congressman;

    private Double rate = 4.5;

    private String content = "좋아요";

    private List<RatingLike> ratingLikeList = new ArrayList<>();

    private List<RatingDislike> ratingDislikeList = new ArrayList<>();

    public static RatingFixture builder() {
        return new RatingFixture();
    }

    public RatingFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public RatingFixture setMember(Member member) {
        this.member = member;
        return this;
    }

    public RatingFixture setCongressman(Congressman congressman) {
        this.congressman = congressman;
        return this;
    }

    public RatingFixture setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    public RatingFixture setContent(String content) {
        this.content = content;
        return this;
    }

    public RatingFixture setRatingLikeList(List<RatingLike> ratingLikeList) {
        this.ratingLikeList = ratingLikeList;
        return this;
    }

    public RatingFixture setRatingDislikeList(
            List<RatingDislike> ratingDislikeList) {
        this.ratingDislikeList = ratingDislikeList;
        return this;
    }

    public Rating build() {
        return Rating.builder()
                .id(id)
                .member(member)
                .congressman(congressman)
                .rate(rate)
                .content(content)
                .ratingLikeList(ratingLikeList)
                .ratingDislikeList(ratingDislikeList)
                .build();
    }

    public static Rating of(Member member, Congressman congressman) {
        return RatingFixture.builder()
                .setMember(member)
                .setCongressman(congressman)
                .build();
    }
}
