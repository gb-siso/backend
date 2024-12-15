package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.MemberControllerDocs;
import com.guenbon.siso.dto.auth.LoginResponse;
import com.guenbon.siso.dto.auth.SignUpRequest;
import com.guenbon.siso.dto.member.*;
import com.guenbon.siso.support.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MemberUpdateFormResponse> updateForm(@LoginId Long loginId) {
        return null;
    }

    @Override
    @PatchMapping
    public ResponseEntity<MemberUpdateResponse> update(@LoginId Long loginId, MemberUpdateRequest memberUpdateRequest) {
        return null;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> withDrawl(@LoginId Long loginId) {
        return null;
    }

    @Override
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoResponse> info(@LoginId Long loginId, @PathVariable String memberId) {
        return null;
    }
}
