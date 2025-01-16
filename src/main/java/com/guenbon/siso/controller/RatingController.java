package com.guenbon.siso.controller;

import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.page.PageParam;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.RatingService;
import com.guenbon.siso.support.annotation.LoginId;
import com.guenbon.siso.support.annotation.page.PageConfig;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final RatingService ratingService;
    private final AESUtil aesUtil;

    @PostMapping
    public void ratingSave(@LoginId Long loginId, @Validated @RequestBody RatingWriteDTO ratingWriteDTO,
                           HttpServletResponse response)
            throws IOException {
        final String encryptedCongressmanId = ratingWriteDTO.getCongressmanId();
        ratingService.create(loginId, aesUtil.decrypt(encryptedCongressmanId));
        response.sendRedirect("/api/v1/congressman/" + encryptedCongressmanId);
    }

    @GetMapping("/{encryptedCongressmanId}")
    public ResponseEntity<RatingListDTO> ratingList(
            @PathVariable String encryptedCongressmanId,
            @PageConfig(allowedSorts = {"like", "dislike", "topicality"},
                    defaultSort = "topicality, DESC", defaultPage = 0, defaultSize = 20) PageParam pageParam,
            @Validated @ModelAttribute CountCursor cursor) {
        return ResponseEntity.ok(
                ratingService.validateAndGetRecentRatings(encryptedCongressmanId, pageParam.toPageable(), cursor)
        );
    }
}
