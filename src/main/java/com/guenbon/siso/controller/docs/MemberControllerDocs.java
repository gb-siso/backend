package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.auth.LoginResponse;
import com.guenbon.siso.dto.auth.SignUpRequest;
import com.guenbon.siso.dto.member.MemberUpdateFormResponse;
import com.guenbon.siso.dto.member.MemberUpdateRequest;
import com.guenbon.siso.dto.member.MemberUpdateResponse;
import com.guenbon.siso.dto.member.SignUpFormResponse;
import com.guenbon.siso.support.annotation.MemberId;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입 폼 요청", description = "카카오 로그인 시도 시 비회원일 경우 해당 url로 리다이렉트해서 폼 내림")
    @Parameters(value = {
            @Parameter(name = "kakaoId", description = "카카오 식별 id")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "siso 회원가입", content = @Content(schema = @Schema(implementation = SignUpFormResponse.class)))
    })
    ResponseEntity<SignUpFormResponse> signUpForm(@RequestParam String kakaoId);

    @Operation(summary = "회원가입", description = "카카오 식별 id, imageUrl, nickname 포함 회원가입 요청")
    @Parameters(value = {
            @Parameter(name = "kakaoId", description = "카카오 회원번호")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "siso 회원가입", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    ResponseEntity<LoginResponse> signUp(@RequestBody SignUpRequest signUpRequest);


    @ApiResponses(value = {
            @ApiResponse(
                    headers = @Header(name = "Authorization", description = "accessToken", schema = @Schema(type = "string")),
                    responseCode = "200",
                    description = "회원 정보 수정 폼",
                    content = @Content(schema = @Schema(implementation = MemberUpdateFormResponse.class))
            )
    })
    ResponseEntity<MemberUpdateFormResponse> updateForm(@MemberId Long memberId);

    @Parameters(value = {
            @Parameter(name = "memberUpdateRequest", description = "회원 수정 요청 정보", schema = @Schema(implementation = MemberUpdateRequest.class))
    })
    @ApiResponses(value = {
            @ApiResponse(
                    headers = @Header(name = "Authorization", description = "accessToken", schema = @Schema(type = "string")),
                    responseCode = "200",
                    description = "회원 정보 수정 결과",
                    content = @Content(schema = @Schema(implementation = MemberUpdateResponse.class))
            )
    })
    ResponseEntity<MemberUpdateResponse> update(@MemberId Long memberId,
                                                @RequestBody MemberUpdateRequest memberUpdateRequest);

    @ApiResponses(value = {
            @ApiResponse(
                    headers = {
                            @Header(
                                    name = "Set-Cookie",
                                    description = "refreshToken",
                                    schema = @Schema(type = "string", example = "refreshToken=xyz789; Path=/; HttpOnly; Secure")
                            ),
                            @Header(
                                    name = "Authorization",
                                    description = "accessToken",
                                    schema = @Schema(type = "string", example = "Bearer abc123")
                            )
                    },
                    responseCode = "200",
                    description = "siso 회원탈퇴",
                    content = @Content())
    })
    ResponseEntity<Void> withDrawl(@MemberId Long memberId);
}
