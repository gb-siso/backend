package com.guenbon.siso.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "로그인 성공 응답 dto")
public class LoginResponse {
    private String accessToken;
}
