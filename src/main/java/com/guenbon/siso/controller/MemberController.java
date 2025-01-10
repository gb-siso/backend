package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.MemberControllerDocs;
import com.guenbon.siso.dto.auth.request.SignUpDTO;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.dto.member.response.MemberDetailDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateFormDTO;
import com.guenbon.siso.dto.member.response.SignUpFormDTO;
import com.guenbon.siso.support.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/member")
public class MemberController implements MemberControllerDocs {

    @Override
    @GetMapping("/signUp")
    public ResponseEntity<SignUpFormDTO> memberAddForm(String kakaoId) {
        return null;
    }

    @Override
    @PostMapping
    public ResponseEntity<LoginDTO> memberAdd(SignUpDTO signUpDTO) {
        return null;
    }

    @Override
    @GetMapping("/update")
    public ResponseEntity<MemberUpdateFormDTO> memberModifyForm(@LoginId Long loginId) {
        return null;
    }

    @Override
    @PatchMapping
    public ResponseEntity<MemberUpdateDTO> memberModify(@LoginId Long loginId, MemberUpdateDTO memberUpdateRequest) {
        return null;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> memberRemove(@LoginId Long loginId) {
        return null;
    }

    @Override
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDetailDTO> memberDetail(@LoginId Long loginId, @PathVariable String memberId) {
        return null;
    }
}
