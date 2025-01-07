package com.guenbon.siso.dto.member.common;

import com.guenbon.siso.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "회원 정보 공통 dto")
public class MemberDTO {
    private String id;
    @Schema(description = "프로필 이미지")
    private String imageUrl;
    @Schema(description = "닉네임")
    private String nickname;

    public static MemberDTO of(Member member, String encryptedId) {
        return new MemberDTO(encryptedId, member.getImageUrl(), member.getNickname());
    }
}
