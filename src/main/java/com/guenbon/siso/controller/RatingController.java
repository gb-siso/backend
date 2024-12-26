package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.RatingControllerDocs;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.support.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ratings")
public class RatingController implements RatingControllerDocs {

    @Override
    @PostMapping
    public ResponseEntity<Void> info(@LoginId Long loginId, RatingWriteDTO ratingWriteDTO) {
        throw new BadRequestException(RatingErrorCode.DUPLICATED);
    }
}
