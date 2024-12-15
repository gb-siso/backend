package com.guenbon.siso.dto.member.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 정보 공통 dto")
public class MemberDTO {
    @Schema(description = "프로필 이미지")
    private String imageUrl;
    @Schema(description = "닉네임")
    private String nickname;
}
