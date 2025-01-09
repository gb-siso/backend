package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.RatingControllerDocs;
import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.RatingService;
import com.guenbon.siso.support.annotation.LoginId;
import com.guenbon.siso.support.util.SortValidator;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController implements RatingControllerDocs {
    private final RatingService ratingService;
    private final AESUtil aesUtil;
    @Value("${spring.siso.domain}")
    private String domain;

    @Override
    @PostMapping
    public void create(@LoginId Long loginId, RatingWriteDTO ratingWriteDTO, HttpServletResponse response)
            throws IOException {
        final String encryptedCongressmanId = ratingWriteDTO.getCongressmanId();
        ratingService.create(loginId, aesUtil.decrypt(encryptedCongressmanId));
        response.sendRedirect(domain + "/api/v1/congressionman/" + encryptedCongressmanId);
    }

    @GetMapping("/{encryptedCongressmanId}")
    public ResponseEntity<RatingListDTO> getRatingListByCongressmanId(
            @PathVariable String encryptedCongressmanId,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = {"topicality"}, direction = Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String idCursor,
            @RequestParam(required = false) Integer countCursor) {
        CountCursor.of(idCursor, countCursor);
        SortValidator.validateSortProperties(pageable.getSort(), List.of("like", "dislike", "topicality"));
        // 비즈니스 로직 (임시 반환값)
        return ResponseEntity.ok(new RatingListDTO());
    }
}
