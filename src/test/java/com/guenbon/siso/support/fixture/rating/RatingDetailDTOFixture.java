package com.guenbon.siso.support.fixture.rating;


import com.guenbon.siso.dto.member.common.MemberDTO;
import com.guenbon.siso.dto.rating.response.RatingDetailDTO;
import com.guenbon.siso.support.fixture.member.MemberDTOFixture;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingDetailDTOFixture {

    private String id = "testId";
    private MemberDTO member = MemberDTOFixture.builder().build();
    private String content = "부적절한 출산율 정책으로 출산율 하락에 큰 기여를 함";
    private Double rate = 2.0;
    private Integer likeCount = 10;
    private Integer dislikeCount = 2;

    public static RatingDetailDTOFixture builder() {
        return new RatingDetailDTOFixture();
    }

    public RatingDetailDTOFixture setId(String id) {
        this.id = id;
        return this;
    }

    public RatingDetailDTOFixture setMember(MemberDTO member) {
        this.member = member;
        return this;
    }

    public RatingDetailDTOFixture setContent(String content) {
        this.content = content;
        return this;
    }

    public RatingDetailDTOFixture setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    public RatingDetailDTOFixture setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public RatingDetailDTOFixture setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
        return this;
    }

    public RatingDetailDTO build() {
        return new RatingDetailDTO(id, member, content, rate, likeCount, dislikeCount);
    }
}
