package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.auth.request.SignUpDTO;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.dto.member.response.MemberDetailDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateFormDTO;
import com.guenbon.siso.dto.member.response.SignUpFormDTO;
import com.guenbon.siso.support.annotation.LoginId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입 폼 요청", description = "카카오 로그인 시도 시 비회원일 경우 해당 url로 리다이렉트해서 폼 내림")
    @Parameters(value = {
            @Parameter(name = "kakaoId", description = "카카오 식별 id")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "siso 회원가입", content = @Content(schema = @Schema(implementation = SignUpFormDTO.class)))
    })
    ResponseEntity<SignUpFormDTO> memberAddForm(@RequestParam String kakaoId);

    @Operation(summary = "회원가입", description = "카카오 식별 id, imageUrl, nickname 포함 회원가입 요청")
    @Parameters(value = {
            @Parameter(name = "kakaoId", description = "카카오 회원번호")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    headers = {
                            @Header(name = "Set-Cookie", description = "refreshToken", schema = @Schema(type = "string", example = "refreshToken=xyz789; Path=/; HttpOnly; Secure")),
                            @Header(name = "Authorization", description = "accessToken", schema = @Schema(type = "string", example = "Authorization=xyz789"))
                    },
                    responseCode = "200", description = "siso 회원가입", content = @Content(schema = @Schema(implementation = LoginDTO.class)))
    })
    ResponseEntity<LoginDTO> memberAdd(@RequestBody SignUpDTO signUpDTO);

    @Operation(summary = "회원 정보 수정 폼 요청", description = "로그인 한 사용자가 회원 수정 정보 폼 요청")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 정보 수정 폼",
                    content = @Content(schema = @Schema(implementation = MemberUpdateFormDTO.class))
            )
    })
    ResponseEntity<MemberUpdateFormDTO> memberModifyForm(@LoginId Long loginId);

    @Operation(summary = "회원 정보 수정 요청", description = "로그인 사용자가 회원 정보 수정 폼 작성해서 수정 요청")
    @Parameters(value = {
            @Parameter(name = "memberUpdateRequest", description = "회원 수정 요청 정보", schema = @Schema(implementation = MemberUpdateDTO.class))
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 정보 수정 결과",
                    content = @Content(schema = @Schema(implementation = MemberUpdateDTO.class))
            )
    })
    ResponseEntity<MemberUpdateDTO> memberModify(@LoginId Long loginid,
                                                 @RequestBody MemberUpdateDTO memberUpdateRequest);

    @Operation(summary = "회원탈퇴 요청", description = "로그인 한 사용자가 회원 탈퇴 요청 (refresh 토큰도 확인)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "siso 회원탈퇴",
                    content = @Content())
    })
    ResponseEntity<Void> memberRemove(@LoginId Long loginId);

    @Operation(summary = "회원정보 요청", description = "회원 정보 요청 (로그인정보=요청 정보일시 본인 정보, 아닐 경우 타인 정보)")
    @Parameters(value = {
            @Parameter(name = "memberId", description = "정보 요청 대상 memberId"),
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 정보보기 페이지(마이페이지 or 타인 페이지)",
                    content = @Content(schema = @Schema(implementation = MemberDetailDTO.class)))
    })
    ResponseEntity<MemberDetailDTO> memberDetail(@LoginId Long loginId, @PathVariable String memberId);
}
