package com.guenbon.siso.service;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CongressmanService {

    private final CongressmanRepository congressmanRepository;

    public Congressman findById(Long id) {
        return congressmanRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(CongressmanErrorCode.NOT_EXISTS));
    }

    public void getList(Pageable pageable, Long cursorId, Double cursorRating, String party, String search) {
        throw new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
    }
}
