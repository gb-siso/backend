package com.guenbon.siso.controller;

import com.guenbon.siso.controller.docs.CongressmanControllerDocs;
import com.guenbon.siso.dto.congressman.response.CongressmanDetailDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/congressionman")
public class CongressionmanController implements CongressmanControllerDocs {

    @Override
    @GetMapping
    public ResponseEntity<CongressmanListDTO> list(String party, Pageable pageable, Long size, Long cursor, String search) {
        return null;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CongressmanDetailDTO> detail(Pageable pageable, Long size, Long cursor, @PathVariable(name = "id") String congressionmanId, Long loginId) {
        return null;
    }
}
