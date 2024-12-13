package com.guenbon.siso.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "회원 가입 요청 dto")
public class SignUpRequest {
    @Schema(description = "카카오 회원 식별용 id")
    private String kakaoId;
    @Schema(description = "프로필 이미지 경로")
    private String imageUrl;
    @Schema(description = "닉네임")
    private String nickname;
}
