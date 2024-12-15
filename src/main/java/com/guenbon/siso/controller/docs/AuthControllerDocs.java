package com.guenbon.siso.controller.docs;

import com.guenbon.siso.dto.auth.response.LoginDTO;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "카카오 로그인", description = "사용자 카카오 로그인 후 해당 url로 인가코드와 함께 리다이렉트")
    @Parameters(value = {
            @Parameter(name = "code", description = "카카오 인가코드")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    headers = @Header(name = "Set-Cookie", description = "refreshToken", schema = @Schema(type = "string", example = "refreshToken=xyz789; Path=/; HttpOnly; Secure")),
                    responseCode = "200",
                    description = "카카오 로그인",
                    content = @Content(schema = @Schema(implementation = LoginDTO.class)))
    })
    ResponseEntity<LoginDTO> kakaoLogin(@RequestParam String code);


    @Operation(summary = "accessToken 재발급", description = "쿠키에 refreshToken 포함 요청으로 accessToken 재발급(response body에 포함), 보안을 위해 refreshToken도 재발급(cookie에 포함)")
    @Parameters(value = {
            @Parameter(name = "refreshToken", description = "accessToken 재발급을 위한 refreshToken")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    headers = @Header(name = "Set-Cookie", description = "refreshToken", schema = @Schema(type = "string", example = "refreshToken=xyz789; Path=/; HttpOnly; Secure")),
                    responseCode = "200",
                    description = "accessToken 재발급",
                    content = @Content(schema = @Schema(implementation = LoginDTO.class)))
    })
    ResponseEntity<LoginDTO> kakaoReissue(@CookieValue("refreshToken") String refreshToken);

    @Operation(summary = "로그아웃", description = "Authorization 헤더에 accessToken 포함 요청으로 로그아웃 처리")
    @ApiResponses(value = {
            @ApiResponse(
                    headers = @Header(name = "Authorization", description = "accessToken", schema = @Schema(type = "string")),
                    responseCode = "200",
                    description = "로그아웃"
                    )
    })
    ResponseEntity<Void> logOut(@LoginId Long loginId);

}
