package com.guenbon.siso.entity;

import com.guenbon.siso.entity.common.DateEntity;
import com.guenbon.siso.entity.dislike.RatingDisLike;
import com.guenbon.siso.entity.like.RatingLike;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Rating extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "congressman_id")
    private Congressman congressman;

    private Double rate;

    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "rating")
    private List<RatingLike> ratingLikeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "rating")
    private List<RatingDisLike> ratingDisLikeList = new ArrayList<>();

    public void addLike(RatingLike ratingLike) {
        ratingLikeList.add(ratingLike);
        ratingLike.setRating(this);
    }

    public void addDisLike(RatingDisLike ratingDisLike) {
        ratingDisLikeList.add(ratingDisLike);
        ratingDisLike.setRating(this);
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
