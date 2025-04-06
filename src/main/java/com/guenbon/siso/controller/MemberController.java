package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.request.SignUpDTO;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.dto.member.response.MemberDetailDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateDTO;
import com.guenbon.siso.dto.member.response.MemberUpdateFormDTO;
import com.guenbon.siso.dto.member.response.SignUpFormDTO;
import com.guenbon.siso.support.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/member")
public class MemberController {

    @GetMapping("/signUp")
    public ResponseEntity<SignUpFormDTO> memberAddForm(String kakaoId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<LoginDTO> memberAdd(SignUpDTO signUpDTO) {
        return null;
    }

    @GetMapping("/update")
    public ResponseEntity<MemberUpdateFormDTO> memberModifyForm(@LoginId Long loginId) {
        return null;
    }

    @PatchMapping
    public ResponseEntity<MemberUpdateDTO> memberModify(@LoginId Long loginId, MemberUpdateDTO memberUpdateRequest) {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Void> memberRemove(@LoginId Long loginId) {
        return null;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDetailDTO> memberDetail(@LoginId Long loginId, @PathVariable String memberId) {
        return null;
    }
}
