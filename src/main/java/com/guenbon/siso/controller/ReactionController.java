package com.guenbon.siso.controller;

import com.guenbon.siso.dto.reaction.response.ReactionDTO;
import com.guenbon.siso.service.reaction.CongressmanDisLikeService;
import com.guenbon.siso.service.reaction.CongressmanLikeService;
import com.guenbon.siso.service.reaction.RatingDisLikeService;
import com.guenbon.siso.service.reaction.RatingLikeService;
import com.guenbon.siso.support.annotation.Login;
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
    private final RatingDisLikeService ratingDisLikeService;
    private final CongressmanLikeService congressmanLikeService;
    private final CongressmanDisLikeService congressmanDisLikeService;

    @Login
    @PostMapping("/likes/rating/{encryptedRatingId}")
    public ResponseEntity<ReactionDTO> createRatingLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingLikeService.create(encryptedRatingId, loginId));
    }

    @Login
    @DeleteMapping("/likes/rating/{encryptedRatingId}")
    public ResponseEntity<ReactionDTO> deleteRatingLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingLikeService.delete(encryptedRatingId, loginId));
    }

    @Login
    @PostMapping("/dislikes/rating/{encryptedRatingId}")
    public ResponseEntity<ReactionDTO> createRatingDisLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingDisLikeService.create(encryptedRatingId, loginId));
    }

    @Login
    @DeleteMapping("/dislikes/rating/{encryptedRatingId}")
    public ResponseEntity<ReactionDTO> deleteRatingDisLike(@PathVariable String encryptedRatingId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(ratingDisLikeService.delete(encryptedRatingId, loginId));
    }

    @Login
    @PostMapping("/likes/congressman/{encryptedCongressmanId}")
    public ResponseEntity<ReactionDTO> createCongressmanLike(@PathVariable String encryptedCongressmanId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(congressmanLikeService.create(encryptedCongressmanId, loginId));
    }

    @Login
    @DeleteMapping("/likes/congressman/{encryptedCongressmanId}")
    public ResponseEntity<ReactionDTO> deleteCongressmanLike(@PathVariable String encryptedCongressmanId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(congressmanLikeService.delete(encryptedCongressmanId, loginId));
    }

    @Login
    @PostMapping("/dislikes/congressman/{encryptedCongressmanId}")
    public ResponseEntity<ReactionDTO> createCongressmanDisLike(@PathVariable String encryptedCongressmanId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(congressmanDisLikeService.create(encryptedCongressmanId, loginId));
    }

    @Login
    @DeleteMapping("/dislikes/congressman/{encryptedCongressmanId}")
    public ResponseEntity<ReactionDTO> deleteCongressmanDisLike(@PathVariable String encryptedCongressmanId, @LoginId Long loginId) {
        return ResponseEntity.ok().body(congressmanDisLikeService.delete(encryptedCongressmanId, loginId));
    }
}
