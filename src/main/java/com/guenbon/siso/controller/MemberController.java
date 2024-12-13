package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.MemberControllerDocs;
import com.guenbon.siso.dto.auth.LoginResponse;
import com.guenbon.siso.dto.auth.SignUpRequest;
import com.guenbon.siso.dto.member.MemberUpdateFormResponse;
import com.guenbon.siso.dto.member.MemberUpdateRequest;
import com.guenbon.siso.dto.member.MemberUpdateResponse;
import com.guenbon.siso.dto.member.SignUpFormResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/member")
public class MemberController implements MemberControllerDocs {

    @Override
    @GetMapping("/signUp")
    public ResponseEntity<SignUpFormResponse> signUpForm(String kakaoId) {
        return null;
    }

    @Override
    @PostMapping
    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        return null;
    }

    @Override
    @GetMapping("/update")
    public ResponseEntity<MemberUpdateFormResponse> updateForm(Long memberId) {
        return null;
    }

    @Override
    @PatchMapping
    public ResponseEntity<MemberUpdateResponse> update(Long memberId, MemberUpdateRequest memberUpdateRequest) {
        return null;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> withDrawl(Long memberId) {
        return null;
    }
}
