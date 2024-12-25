package com.guenbon.siso.service;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.repository.CongressmanRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CongressmanService {

    private final CongressmanRepository congressmanRepository;

    public Congressman findById(Long id){
        Optional<Congressman> congressman = congressmanRepository.findById(id);
        return congressman.get();
    }
}
