package com.guenbon.siso.service;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
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

    private final AESUtil aesUtil;

    private final CongressmanRepository congressmanRepository;

    public Congressman findById(Long id) {
        return congressmanRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(CongressmanErrorCode.NOT_EXISTS));
    }

    private List<CongressmanGetListDTO> getCongressmanGetListDTOList(Pageable pageable, Long cursorId,
                                                                     Double cursorRating, String party,
                                                                     String search) {
        if (pageable == null || cursorId == null) {
            throw new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
        return congressmanRepository.getList(pageable, cursorId, cursorRating, party, search);
    }

    private List<String> getRatedMemberImageList(final Long id) {
        ensureIdExists(id);
        return congressmanRepository.getRecentMemberImagesByCongressmanId(id);
    }

    public CongressmanListDTO getCongressmanListDTO(final Pageable pageable, final String cursorId,
                                                    final Double cursorRate, final String party, final String search) {
        final long decryptedCursorId = aesUtil.decrypt(cursorId);
        final List<CongressmanGetListDTO> congressmanGetListDTOList = getCongressmanGetListDTOList(
                pageable, decryptedCursorId, cursorRate, party, search);

        final List<CongressmanDTO> congressmanDTOList = convertToCongressmanDTOList(congressmanGetListDTOList);

        return buildCongressmanListDTO(pageable, congressmanDTOList);
    }

    private List<CongressmanDTO> convertToCongressmanDTOList(List<CongressmanGetListDTO> congressmanGetListDTOList) {
        return congressmanGetListDTOList.stream()
                .map(congressmanGetListDTO -> CongressmanDTO.of(
                        aesUtil.encrypt(congressmanGetListDTO.getId()),
                        congressmanGetListDTO,
                        getRatedMemberImageList(congressmanGetListDTO.getId())))
                .toList();
    }

    private CongressmanListDTO buildCongressmanListDTO(Pageable pageable, List<CongressmanDTO> congressmanDTOList) {
        final int pageSize = pageable.getPageSize();
        final CongressmanListDTO congressmanListDTO = CongressmanListDTO.builder()
                .congressmanList(congressmanDTOList)
                .build();

        if (congressmanDTOList.size() <= pageSize) {
            congressmanListDTO.setLastPage(true);
        } else {
            final CongressmanDTO lastElement = congressmanDTOList.get(pageSize);
            congressmanListDTO.setIdCursor(lastElement.getId());
            congressmanListDTO.setRateCursor(lastElement.getRate());
            congressmanListDTO.setLastPage(false);
        }

        return congressmanListDTO;
    }


    private void ensureIdExists(final Long id) {
        if (id == null) {
            throw new InternalServerException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
        if (!congressmanRepository.existsById(id)) {
            throw new InternalServerException(CongressmanErrorCode.NOT_EXISTS);
        }
    }
}
