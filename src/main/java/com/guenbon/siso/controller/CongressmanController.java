package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.CongressmanControllerDocs;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.support.util.SortValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
public class CongressmanController implements CongressmanControllerDocs {

    private final AESUtil aesUtil;
    private final CongressmanService congressmanService;

    @Override
    @GetMapping
    public ResponseEntity<CongressmanListDTO> congressmanList(
            @PageableDefault(page = 0, size = 20, sort = {"rate"}, direction = Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "") String cursorId,
            @RequestParam(required = false) Double cursorRate,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String search) {
        SortValidator.validateSortProperties(pageable.getSort(), List.of("rate"));
        return ResponseEntity.ok(
                congressmanService.getCongressmanListDTO(pageable, setCursorIdIfEmpty(cursorId), cursorRate, party,
                        search));
    }

    private String setCursorIdIfEmpty(String cursorId) {
        return cursorId.isEmpty() ? aesUtil.encrypt(Long.MAX_VALUE) : cursorId;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<String> congressmanDetail(Pageable pageable, Long cursor,
                                                    @PathVariable(name = "id") String congressmanId, Long loginId) {
        return ResponseEntity.ok("평가 작성 성공 후 리다이렉트됨");
    }
}
