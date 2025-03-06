package com.guenbon.siso.controller;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.service.reaction.RatingLikeService;
import com.guenbon.siso.support.annotation.LoginId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ReactionController {

    private final RatingLikeService ratingLikeService;

    @PostMapping("/likes/rating/{encryptedRatingId}")
    public ResponseEntity<RatingReactionDTO> createRatingLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingLikeService.create(encryptedRatingId, loginId));
    }

    @DeleteMapping("/likes/rating/{encryptedRatingId}")
    public ResponseEntity<RatingReactionDTO> deleteRatingLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingLikeService.delete(encryptedRatingId, loginId));
    }
}
