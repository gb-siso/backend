package com.guenbon.siso.dto.member.request;

import com.guenbon.siso.dto.member.common.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 수정 요청 DTO")
public class MemberUpdateDTO {
    @Schema(description = "수정 요청 정보")
    private MemberDTO memberDTO;
}
