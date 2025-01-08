package com.guenbon.siso.dto.rating.response;

import com.guenbon.siso.dto.member.common.MemberDTO;
import com.guenbon.siso.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public static RatingDetailDTO from(Rating rating, MemberDTO member, String encryptedId) {
        return new RatingDetailDTO(encryptedId, member, rating.getContent(), rating.getRate(), rating.getLikeCount(),
                rating.getDislikeCount());
    }

    public Integer getTopicality() {
        return likeCount + dislikeCount;
    }
}