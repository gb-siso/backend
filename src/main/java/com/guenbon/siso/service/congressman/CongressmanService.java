package com.guenbon.siso.service.congressman;

import com.guenbon.siso.dto.congressman.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.SyncCongressmanDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.entity.congressman.AssemblySession;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.repository.assemblysession.AssemblySessionRepository;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final AssemblySessionRepository assemblySessionRepository;

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

    // Assembly session : cascade all 에 의해 자동 삭제
    private int batchRemoveCongressman(List<Congressman> toDelete) {
        List<Long> idList = toDelete.stream().map(Congressman::getId).collect(Collectors.toList());
        return congressmanRepository.batchDelete(idList);
    }

    private List<Congressman> batchInsertCongressman(List<Congressman> toInsertAndUpdate) {
        if (toInsertAndUpdate.isEmpty()) return new ArrayList<>();
        List<Congressman> congressmen = congressmanRepository.saveAll(toInsertAndUpdate);
        return congressmen;
    }

    public List<Congressman> getCongressmanList() {
        return congressmanRepository.findAll();
    }

    /**
     * @param recentSyncList 최신 국회의원+대수 데이터
     * @return
     */
    @Transactional(readOnly = false)
    public CongressmanBatchResultDTO syncCongressman(List<SyncCongressmanDTO> recentSyncList) {

        List<Congressman> dbCongressmanList = getCongressmanList();
        List<AssemblySession> dbAssemblySessionList = getAssemblySessionList();

        Map<Long, List<Integer>> idAssemblySessionMap = new HashMap<>();
        dbAssemblySessionList.forEach(assemblySession -> {
            final Long congressmanId = assemblySession.getCongressman().getId();

            // 리스트를 가져오거나 없으면 새로 생성
            List<Integer> sessionList = idAssemblySessionMap.getOrDefault(congressmanId, new ArrayList<>());

            // 리스트에 세션 추가
            sessionList.add(assemblySession.getSession());

            // 맵에 리스트를 다시 넣어줌 (이미 존재하는 키라면 덮어쓰기)
            idAssemblySessionMap.put(congressmanId, sessionList);
        });

        // db 국회의원+대수 데이터
        List<SyncCongressmanDTO> dbSyncList = dbCongressmanList
                .stream()
                .map(congressman -> SyncCongressmanDTO.of(congressman, idAssemblySessionMap.get(congressman.getId()))).toList();
        Map<String, SyncCongressmanDTO> dbSyncMap = dbSyncList.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getCongressman().getCode(), // 키: SyncCongressmanDTO의 의원 코드
                        dto -> dto // 값: SyncCongressmanDTO 자체
                ));

        List<Congressman> toInsert = new ArrayList<>();
        List<Congressman> toDelete = new ArrayList<>(dbCongressmanList);
        Map<String, List<Integer>> codeSessionMapToInsert = new HashMap<>();
        List<AssemblySession> assemblySessionToUpdate = new ArrayList<>();

        int updateCount = 0;

        for (SyncCongressmanDTO recentSyncDTO : recentSyncList) {

            Congressman recentCongressman = recentSyncDTO.getCongressman();
            List<Integer> recentAssemblySessions = recentSyncDTO.getAssemblySessions();

            SyncCongressmanDTO dbSyncDTO = dbSyncMap.get(recentCongressman.getCode());
            Congressman dbCongressman = dbSyncDTO.getCongressman();
            List<Integer> dbAssemblySessions = dbSyncDTO.getAssemblySessions();

            if (dbCongressman == null) {
                toInsert.add(recentCongressman);
                codeSessionMapToInsert.put(recentCongressman.getCode(), recentAssemblySessions);
            } else {
                if (!equalsWithoutId(recentCongressman, dbCongressman)) {
                    // 변경감지에 의해 update 됨
                    dbCongressman.updateFieldsFrom(recentCongressman);
                    updateCount++;
                }

                if (dbAssemblySessions.equals(recentAssemblySessions)) {
                    // todo 대수가 다를 경우 어떻게 처리 ?
                }

                toDelete.remove(dbCongressman);
            }
        }

        List<Congressman> batchInsertResult = batchInsertCongressman(toInsert);
        int batchRemoveResultCount = batchRemoveCongressman(toDelete);


        // AssemblySession 배치 처리
        // delete : cascade.all로 congressman 삭제되면 삭제됨

        // 대수 배치 insert 처리
        List<AssemblySession> assemblySessionsToInsert = new ArrayList<>();
        batchInsertResult.forEach(insertedCongressman -> {
                    List<Integer> sessionListToInsert = codeSessionMapToInsert.get(insertedCongressman.getCode());
                    for (Integer session : sessionListToInsert) {
                        assemblySessionsToInsert.add(AssemblySession.of(insertedCongressman, session));
                    }
                }
        );
        List<AssemblySession> batchAssemblySessionInsertResult = batchInsertAssemblySession(assemblySessionsToInsert);

        // 대수 배치 update 처리




        log.info("syncCongressman 국회의원 정보 동기화 정상 처리 완료");
        log.info(" 삽입 수 : " + batchInsertResult.size());
        log.info(" 수정 수 : " + updateCount);
        log.info(" 삭제 수 : " + batchRemoveResultCount);

        return CongressmanBatchResultDTO.of(LocalDateTime.now(), batchInsertResult.stream().map(this::from).toList(), updateCount, batchRemoveResultCount);
    }

    private List<AssemblySession> batchInsertAssemblySession(List<AssemblySession> assemblySessionsToInsert) {
        return assemblySessionRepository.saveAll(assemblySessionsToInsert);
    }

    private List<AssemblySession> getAssemblySessionList() {
        return assemblySessionRepository.findAll();
    }

    public CongressmanBatchResultDTO.CongressmanDTO from(Congressman congressman) {
        return new CongressmanBatchResultDTO.CongressmanDTO(aesUtil.encrypt(congressman.getId()), congressman.getCode(), congressman.getName());
    }

    private boolean equalsWithoutId(Congressman recentCongressman, Congressman dbCongressman) {
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
//                (recentCongressman.getAssemblySessions() == null ? dbCongressman.getAssemblySessions() == null : recentCongressman.getAssemblySessions().equals(dbCongressman.getAssemblySessions())) &&
                (recentCongressman.getSex() == null ? dbCongressman.getSex() == null : recentCongressman.getSex().equals(dbCongressman.getSex())) &&
                (recentCongressman.getImageUrl() == null ? dbCongressman.getImageUrl() == null : recentCongressman.getImageUrl().equals(dbCongressman.getImageUrl()));
    }
}

