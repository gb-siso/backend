package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.AuthControllerDocs;
import com.guenbon.siso.dto.auth.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController implements AuthControllerDocs {

    @Override
    @GetMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(String code) {
        return null;
    }

    @Override
    @PostMapping
    public ResponseEntity<LoginResponse> kakaoReissue(String refreshToken) {
        return null;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> logOut(Long memberId) {
        return null;
    }
}
