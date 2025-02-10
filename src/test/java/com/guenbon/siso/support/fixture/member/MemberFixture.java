package com.guenbon.siso.support.fixture.member;

import com.guenbon.siso.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberFixture {
    private Long id;
    private Long kakaoId;
    private String nickname = "장몽이";
    private String imageUrl = "myProfileImage";
    private String refreshToken = null;

    public static MemberFixture builder() {
        return new MemberFixture();
    }

    public MemberFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public MemberFixture setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public MemberFixture setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public MemberFixture setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public MemberFixture setKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public Member build() {
        return Member.builder()
                .id(id)
                .kakaoId(kakaoId)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .refreshToken(refreshToken)
                .build();
    }

    public static Member fromId(Long id) {
        return MemberFixture.builder()
                .setId(id)
                .build();
    }
}
