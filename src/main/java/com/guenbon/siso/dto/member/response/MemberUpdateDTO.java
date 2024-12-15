package com.guenbon.siso.dto.member.response;

import com.guenbon.siso.dto.member.common.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "회원 수정 후 응답 dto")
public class MemberUpdateDTO {
    @Schema(description = "수정 후 회원정보")
    private MemberDTO memberDTO;
}
