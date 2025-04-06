package com.guenbon.siso.dto.rating.response;

import com.guenbon.siso.dto.member.common.MemberDTO;
import com.guenbon.siso.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RatingDetailDTO {
    private String id;
    private MemberDTO member;
    private String content;
    private Double rate;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private Boolean pushedLike;
    private Boolean pushedDislike;

    public static RatingDetailDTO from(Rating rating, MemberDTO member, String encryptedId, Boolean pushedLike, Boolean pushedDislike) {
        return new RatingDetailDTO(encryptedId, member, rating.getContent(), rating.getRate(), rating.getLikeCount(),
                rating.getDislikeCount(), rating.getCreatedDate(), pushedLike, pushedDislike);
    }

    public Integer getTopicality() {
        return likeCount + dislikeCount;
    }
}