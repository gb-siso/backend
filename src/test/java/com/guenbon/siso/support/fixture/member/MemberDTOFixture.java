package com.guenbon.siso.support.fixture.member;

import com.guenbon.siso.dto.member.common.MemberDTO;

public class MemberDTOFixture {

    private String id = "testId";
    private String imageUrl = "https://example.com/profile.jpg";
    private String nickname = "TestUser";

    public static MemberDTOFixture builder() {
        return new MemberDTOFixture();
    }

    public MemberDTOFixture setId(String id) {
        this.id = id;
        return this;
    }

    public MemberDTOFixture setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public MemberDTOFixture setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public MemberDTO build() {
        return new MemberDTO(id, imageUrl, nickname);
    }
}
