package com.guenbon.siso.controller;

import static com.guenbon.siso.support.constants.SortProperty.RATE;

import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.support.annotation.page.PageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/congressman")
@RequiredArgsConstructor
public class CongressmanController {

    private final AESUtil aesUtil;
    private final CongressmanService congressmanService;

    @GetMapping
    public ResponseEntity<CongressmanListDTO> congressmanList(
            @PageConfig(allowedSorts = {RATE}, defaultSort = "rate, DESC") Pageable pageable,
            @RequestParam(defaultValue = "") String cursorId,
            @RequestParam(required = false) Double cursorRate,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                congressmanService.getCongressmanListDTO(pageable, setCursorIdIfEmpty(cursorId),
                        cursorRate, party,
                        search));
    }

    private String setCursorIdIfEmpty(String cursorId) {
        return cursorId.isEmpty() ? aesUtil.encrypt(Long.MAX_VALUE) : cursorId;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> congressmanDetail(Pageable pageable, Long cursor,
                                                    @PathVariable(name = "id") String congressmanId, Long loginId) {
        return ResponseEntity.ok("평가 작성 성공 후 리다이렉트됨");
    }
}
