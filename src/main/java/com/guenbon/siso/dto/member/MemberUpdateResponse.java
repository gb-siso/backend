package com.guenbon.siso.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 수정 요청 dto")
public class MemberUpdateResponse {
    @Schema(description = "프로필 이미지 경로")
    private String imageUrl;
    @Schema(description = "닉네임")
    private String nickname;
}
