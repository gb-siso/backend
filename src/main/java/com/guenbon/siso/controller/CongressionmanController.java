package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.CongressmanControllerDocs;
import com.guenbon.siso.dto.congressman.CongressmanListDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CongressionmanController implements CongressmanControllerDocs {

    @Override
    @GetMapping("/congressionman/list")
    public ResponseEntity<CongressmanListDTO> congressmanList(String party, Pageable pageable, Long size, Long cursor) {
        return null;
    }
}
