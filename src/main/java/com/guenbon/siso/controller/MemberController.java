package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.MemberControllerDocs;
import com.guenbon.siso.dto.auth.response.LoginResponse;
import com.guenbon.siso.dto.auth.request.SignUpRequest;
import com.guenbon.siso.dto.member.response.MemberInfoDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateFormDTO;
import com.guenbon.siso.dto.member.response.SignUpFormDTO;
import com.guenbon.siso.support.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/member")
public class MemberController implements MemberControllerDocs {

    @Override
    @GetMapping("/signUp")
    public ResponseEntity<SignUpFormDTO> signUpForm(String kakaoId) {
        return null;
    }

    @Override
    @PostMapping
    public ResponseEntity<LoginResponse> signUp(SignUpRequest signUpRequest) {
        return null;
    }

    @Override
    @GetMapping("/update")
    public ResponseEntity<MemberUpdateFormDTO> updateForm(@LoginId Long loginId) {
        return null;
    }

    @Override
    @PatchMapping
    public ResponseEntity<MemberUpdateDTO> update(@LoginId Long loginId, MemberUpdateDTO memberUpdateRequest) {
        return null;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> withDrawl(@LoginId Long loginId) {
        return null;
    }

    @Override
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoDTO> info(@LoginId Long loginId, @PathVariable String memberId) {
        return null;
    }
}
