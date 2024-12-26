package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.RatingControllerDocs;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.RatingService;
import com.guenbon.siso.support.annotation.LoginId;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ratings")
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
}
