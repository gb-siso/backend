package com.guenbon.siso.dto.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 수정 폼 응답 dto")
public class MemberUpdateFormResponse {
    @Schema(description = "기존 프로필 이미지 경로")
    private String imageUrl;
    @Schema(description = "기존 닉네임")
    private String nickname;
}
