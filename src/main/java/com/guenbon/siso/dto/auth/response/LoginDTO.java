package com.guenbon.siso.dto.auth.response;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 성공 응답 dto")
@ToString
public class LoginDTO {
    private String nickname;
    private String imageUrl;
    private String accessToken;

    public static LoginDTO from(IssueTokenResult issueTokenResult) {
        return new LoginDTO(issueTokenResult.getNickname(), issueTokenResult.getImage(),
                issueTokenResult.getAccessToken());
    }
}
