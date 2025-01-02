package com.guenbon.siso.service;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import java.util.List;
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

    public List<CongressmanGetListDTO> getList(Pageable pageable, Long cursorId, Double cursorRating, String party,
                                               String search) {
        if (pageable == null || cursorId == null) {
            throw new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
        List<CongressmanGetListDTO> list = congressmanRepository.getList(pageable, cursorId, cursorRating, party,
                search);

        return congressmanRepository.getList(pageable, cursorId, cursorRating, party, search);
    }

    public List<String> getRecentRatedMembersImages(final Long id) {
        if (!congressmanRepository.existsById(id)) {
            throw new InternalServerException(CongressmanErrorCode.NOT_EXISTS);
        }
        return null;
    }
}
