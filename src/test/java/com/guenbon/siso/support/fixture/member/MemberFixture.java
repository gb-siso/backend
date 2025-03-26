package com.guenbon.siso.support.fixture.member;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.support.constants.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberFixture {
    private Long id;
    private Long kakaoId;
    private String naverId;
    private String nickname = "장몽이";
    private String imageUrl = "myProfileImage";
    private String refreshToken = null;
    private MemberRole role = MemberRole.MEMBER;

    public static MemberFixture builder() {
        return new MemberFixture();
    }

    public MemberFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public MemberFixture setNaverId(String naverId) {
        this.naverId = naverId;
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

    public MemberFixture setRole(MemberRole role) {
        this.role = role;
        return this;
    }

    public Member build() {
        return Member.builder()
                .id(id)
                .kakaoId(kakaoId)
                .naverId(naverId)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .refreshToken(refreshToken)
                .role(role)
                .build();
    }

    public static Member fromId(Long id) {
        return MemberFixture.builder()
                .setId(id)
                .build();
    }
}
