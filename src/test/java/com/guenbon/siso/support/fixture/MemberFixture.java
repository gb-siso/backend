package com.guenbon.siso.support.fixture;

import com.guenbon.siso.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberFixture {
    private Long id;
    private String nickname;
    private String imageUrl = null;
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

    public Member build() {
        return Member.builder()
                .id(id)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .refreshToken(refreshToken)
                .build();
    }
}
