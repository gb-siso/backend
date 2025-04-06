package com.guenbon.siso.entity;

import com.guenbon.siso.entity.common.DateEntity;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.entity.like.RatingLike;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Rating extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "congressman_id", nullable = false)
    private Congressman congressman;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "rating")
    private List<RatingLike> ratingLikeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "rating")
    private List<RatingDislike> ratingDislikeList = new ArrayList<>();

    public Integer getLikeCount() {
        return ratingLikeList.size();
    }

    public Integer getDislikeCount() {
        return ratingDislikeList.size();
    }

    public Integer getTopicality() {
        return ratingLikeList.size() + ratingDislikeList.size();
    }

    public void addLike(RatingLike ratingLike) {
        ratingLikeList.add(ratingLike);
        ratingLike.setRating(this);
    }

    public void addDislike(RatingDislike ratingDislike) {
        ratingDislikeList.add(ratingDislike);
        ratingDislike.setRating(this);
    }

    public void removeLike(RatingLike ratingLike) {
        ratingLikeList.remove(ratingLike);
    }

    public void removeDislike(RatingDislike ratingDislike) {
        ratingDislikeList.remove(ratingDislike);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", member=" + member +
                ", congressman=" + congressman +
                ", rate=" + rate +
                ", createdAt=" + getCreatedDate()
                +
                '}';
    }
}