package com.guenbon.siso.controller;

import com.guenbon.siso.dto.bill.BillListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.service.congressman.CongressmanApiService;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.support.annotation.page.PageConfig;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.guenbon.siso.support.constants.SortProperty.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/congressman")
@RequiredArgsConstructor
public class CongressmanController {

    private final AESUtil aesUtil;
    private final CongressmanService congressmanService;
    private final CongressmanApiService congressmanApiService;

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

    @GetMapping("/news/{congressmanId}")
    public ResponseEntity<NewsListDTO> newsList(@PathVariable String congressmanId,
                                                @PageConfig(allowedSorts = REG_DATE, defaultSort = "regDate, DESC") Pageable pageable) {
        return ResponseEntity.ok(congressmanApiService.findNewsList(congressmanId, pageable));
    }

    @GetMapping("/bills/{congressmanId}")
    public ResponseEntity<BillListDTO> billList(@PathVariable String congressmanId,
                                                @PageConfig(allowedSorts = PROPOSE_DATE, defaultSort = "proposeDate, DESC") Pageable pageable) {
        return ResponseEntity.ok(congressmanApiService.findBillList(congressmanId, pageable));
    }
}
