package com.guenbon.siso.dto.member.response;


import com.guenbon.siso.dto.member.common.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 수정 폼 응답 dto")
public class MemberUpdateFormDTO {
    @Schema(description = "수정 전 회원정보")
    private MemberDTO memberDTO;
}
