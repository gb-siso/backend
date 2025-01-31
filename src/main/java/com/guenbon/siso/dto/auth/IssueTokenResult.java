package com.guenbon.siso.dto.auth;

import com.guenbon.siso.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
@ToString
public class IssueTokenResult {
    private String refreshTokenCookie;
    private String accessToken;
    private String image;
    private String nickname;

    public static IssueTokenResult of(String accessToken, ResponseCookie refreshTokenCookie, Member member) {
        return IssueTokenResult.builder()
                .refreshTokenCookie(refreshTokenCookie.toString())
                .accessToken(accessToken)
                .nickname(member.getNickname())
                .image(member.getImageUrl())
                .build();
    }
}