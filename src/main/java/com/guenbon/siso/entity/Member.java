package com.guenbon.siso.entity;

import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String nickname;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "refresh_token")
    private String refreshToken;

    public void storeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Member from(Long kakaoId, String nickname, String imageUrl) {
        return Member.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .imageUrl(imageUrl).build();
    }
}
