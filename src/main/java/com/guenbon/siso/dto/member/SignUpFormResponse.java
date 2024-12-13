package com.guenbon.siso.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원가입 ")
public class SignUpFormResponse {
    @Schema(description = "카카오 식별 id, 프론트에서 회원가입 요청 시 해당 데이터 그대로 포함해서 요청")
    private String kakaoId;
}
