package com.guenbon.siso.service.congressman;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CongressmanService {
    private final AESUtil aesUtil;
    private final CongressmanRepository congressmanRepository;

    public Congressman findById(final Long id) {
        return congressmanRepository.findById(id)
                .orElseThrow(() -> new CustomException(CongressmanErrorCode.NOT_EXISTS));
    }

    public CongressmanListDTO getCongressmanListDTO(final Pageable pageable, final String cursorId,
                                                    final Double cursorRate, final String party, final String search) {
        final long decryptedCursorId = aesUtil.decrypt(cursorId);
        final List<CongressmanGetListDTO> congressmanGetListDTOList = getCongressmanGetListDTOList(
                pageable, decryptedCursorId, cursorRate, party, search);

        final List<CongressmanDTO> congressmanDTOList = convertToCongressmanDTOList(congressmanGetListDTOList);

        return buildCongressmanListDTO(pageable, congressmanDTOList);
    }

    public Congressman getCongressman(String encryptedCongressmanId) {
        log.info("!! getCongressman 실제 호출됨 !!");
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        return findById(congressmanId);
    }

    private List<CongressmanGetListDTO> getCongressmanGetListDTOList(Pageable pageable, Long cursorId,
                                                                     Double cursorRating, String party,
                                                                     String search) {
        return congressmanRepository.getList(pageable, cursorId, cursorRating, party, search);
    }

    private List<String> getRatedMemberImageList(final Long id) {
        ensureIdExists(id);
        return congressmanRepository.getRecentMemberImagesByCongressmanId(id);
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
        if (!congressmanRepository.existsById(id)) {
            throw new CustomException(CongressmanErrorCode.NOT_EXISTS);
        }
    }

    public int batchRemoveCongressman(List<Congressman> toDelete) {
        List<Long> idList = toDelete.stream().map(Congressman::getId).collect(Collectors.toList());
        return congressmanRepository.batchDelete(idList);
    }

    public List<Congressman> batchInsertAndUpdateCongressman(List<Congressman> toInsertAndUpdate) {
        if (toInsertAndUpdate.isEmpty()) return Collections.emptyList();
        return congressmanRepository.saveAll(toInsertAndUpdate);  // Batch Insert 실행
    }

    public List<Congressman> getCongressmanList() {
        return congressmanRepository.findAll();
    }

    /**
     * @param recentCongressmanList 국회 api 로 가져온 최신 국회의원 목록
     */
    @Transactional(readOnly = false)
    public CongressmanBatchResultDTO syncCongressman(List<Congressman> recentCongressmanList) {
        // congressman 테이블에서 국회의원 목록 가져오기
        List<Congressman> dbCongressmanList = getCongressmanList();

        Map<String, Congressman> dbCongressmanMap = dbCongressmanList.stream()
                .collect(Collectors.toMap(Congressman::getCode, congressman -> congressman));

        List<Congressman> toInsertAndUpdate = new ArrayList<>();
        List<Congressman> toDelete = new ArrayList<>(dbCongressmanList);

        for (Congressman recentCongressman : recentCongressmanList) {

            Congressman dbCongressman = dbCongressmanMap.get(recentCongressman.getCode());

            if (dbCongressman == null) {
                toInsertAndUpdate.add(recentCongressman);
            } else {
                if (!equalsWithoutId(recentCongressman, dbCongressman)) {
                    toInsertAndUpdate.add(dbCongressman.updateFieldsFrom(recentCongressman));
                }
                toDelete.remove(dbCongressman);
            }
        }

        // 삽입, 수정
        List<Congressman> bathInsertAndUpdateResult = batchInsertAndUpdateCongressman(toInsertAndUpdate);
        // 삭제
        int batchRemoveResultCount = batchRemoveCongressman(toDelete);

        log.info("syncCongressman 국회의원 정보 동기화 정상 처리 완료");
        log.info(" 삽입 & 수정 수 : " + bathInsertAndUpdateResult.size());
        log.info(" 삭제 수 : " + batchRemoveResultCount);

        return CongressmanBatchResultDTO.of(LocalDateTime.now(), bathInsertAndUpdateResult.stream().map(this::from).toList(), batchRemoveResultCount);
    }

    public CongressmanBatchResultDTO.CongressmanDTO from(Congressman congressman) {
        return new CongressmanBatchResultDTO.CongressmanDTO(aesUtil.encrypt(congressman.getId()), congressman.getCode(), congressman.getName());
    }

    public boolean equalsWithoutId(Congressman recentCongressman, Congressman dbCongressman) {
        if (recentCongressman == dbCongressman) {
            return true;  // 두 객체가 동일한 경우
        }

        if (recentCongressman == null || dbCongressman == null) {
            return false;  // 한 객체가 null인 경우
        }

        return recentCongressman.getName().equals(dbCongressman.getName()) &&
                recentCongressman.getParty().equals(dbCongressman.getParty()) &&
                recentCongressman.getTimesElected().equals(dbCongressman.getTimesElected()) &&
                recentCongressman.getCode().equals(dbCongressman.getCode()) &&
                (recentCongressman.getPosition() == null ? dbCongressman.getPosition() == null : recentCongressman.getPosition().equals(dbCongressman.getPosition())) &&
                (recentCongressman.getElectoralDistrict() == null ? dbCongressman.getElectoralDistrict() == null : recentCongressman.getElectoralDistrict().equals(dbCongressman.getElectoralDistrict())) &&
                (recentCongressman.getElectoralType() == null ? dbCongressman.getElectoralType() == null : recentCongressman.getElectoralType().equals(dbCongressman.getElectoralType())) &&
                (recentCongressman.getAssemblySessions() == null ? dbCongressman.getAssemblySessions() == null : recentCongressman.getAssemblySessions().equals(dbCongressman.getAssemblySessions())) &&
                (recentCongressman.getSex() == null ? dbCongressman.getSex() == null : recentCongressman.getSex().equals(dbCongressman.getSex())) &&
                (recentCongressman.getImageUrl() == null ? dbCongressman.getImageUrl() == null : recentCongressman.getImageUrl().equals(dbCongressman.getImageUrl()));
    }
}

