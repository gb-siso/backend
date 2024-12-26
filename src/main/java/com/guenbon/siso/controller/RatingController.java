package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.RatingControllerDocs;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.MemberService;
import com.guenbon.siso.support.annotation.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ratings")
@RequiredArgsConstructor
public class RatingController implements RatingControllerDocs {

    private final MemberService memberService;
    private final CongressmanService congressmanService;
    private final AESUtil aesUtil;


    @Override
    @PostMapping
    public ResponseEntity<Void> create(@LoginId Long loginId, RatingWriteDTO ratingWriteDTO) {
        final Member member = memberService.findById(loginId);
        final Long decryptedCongressmanId = aesUtil.decrypt(ratingWriteDTO.getCongressmanId());
        final Congressman congressman = congressmanService.findById(decryptedCongressmanId);
        throw new BadRequestException(RatingErrorCode.DUPLICATED);
    }
}
