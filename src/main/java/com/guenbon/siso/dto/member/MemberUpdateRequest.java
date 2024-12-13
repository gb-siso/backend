package com.guenbon.siso.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 수정 응답 dto")
public class MemberUpdateRequest {
    @Schema(description = "프로필 이미지 경로")
    private String imageUrl;
    @Schema(description = "닉네임")
    private String nickname;
}
