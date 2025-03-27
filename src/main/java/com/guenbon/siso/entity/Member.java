package com.guenbon.siso.entity;

import com.guenbon.siso.entity.common.DateEntity;
import com.guenbon.siso.support.constants.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class Member extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "kakao_id", unique = true)
    private Long kakaoId;
    @Column(name = "naver_id", unique = true)
    private String naverId;
    @Column(nullable = false)
    private String nickname;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.MEMBER;

    public void storeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Member from(Long kakaoId, String nickname, String imageUrl) {
        return Member.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .imageUrl(imageUrl).build();
    }

    public static Member from(String naverId, String nickname, String imageUrl) {
        return Member.builder()
                .naverId(naverId)
                .nickname(nickname)
                .imageUrl(imageUrl).build();
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }
}
