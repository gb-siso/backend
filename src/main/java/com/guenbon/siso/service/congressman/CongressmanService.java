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
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.service.assemblysession.AssemblySessionService;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CongressmanService {
    private final AESUtil aesUtil;
    private final CongressmanRepository congressmanRepository;
    private final AssemblySessionService assemblySessionService;

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
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        return findById(congressmanId);
    }

    private List<CongressmanGetListDTO> getCongressmanGetListDTOList(Pageable pageable, Long cursorId,
                                                                     Double cursorRating, String party,
                                                                     String search) {
        int pageSize = pageable.getPageSize();
        List<CongressmanGetListDTO> initialList = congressmanRepository.getList(PageRequest.of(pageable.getPageNumber(), pageSize + 1, pageable.getSort()), cursorId, cursorRating, party, search);
        int initialListSize = initialList.size();
        if (isAdditionalFetchNeeded(initialListSize, pageSize)) {
            Long initialListCursorId = cursorId;
            if (!initialList.isEmpty()) {
                initialListCursorId = initialList.get(initialListSize - 1).getId();
            }
            List<CongressmanGetListDTO> additionalList = fetchAdditionalCongressmen(initialListSize, pageable, party, search, initialListCursorId);
            initialList.addAll(additionalList);
        }
        return initialList;
    }

    private boolean isAdditionalFetchNeeded(int initialListSize, int pageSize) {
        return initialListSize < (pageSize + 1);
    }

    private List<CongressmanGetListDTO> fetchAdditionalCongressmen(int initialListSize, Pageable pageable, String party, String search, Long cursorId) {
        int remainingSize = pageable.getPageSize() - initialListSize + 1;
        log.info("fetchAdditionalCongressmen 호출 , remainingSize : {}", remainingSize);
        PageRequest additionalPageRequest = PageRequest.of(pageable.getPageNumber(), remainingSize, pageable.getSort());
        return congressmanRepository.getList(additionalPageRequest, cursorId, null, party, search);
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

    private int batchRemoveCongressman(List<Congressman> toDelete) {
        List<Long> idList = toDelete.stream().map(Congressman::getId).collect(Collectors.toList());
        return congressmanRepository.batchDelete(idList);
    }

    private List<Congressman> batchInsertCongressman(List<Congressman> toInsertAndUpdate) {
        if (toInsertAndUpdate.isEmpty()) {
            log.info("no congressman to insert");
            return new ArrayList<>();
        }
        return congressmanRepository.saveAll(toInsertAndUpdate);
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
        Map<Long, Set<Integer>> idAssemblySessionMap = getIdAssemblySessionMap();

        // db 국회의원+대수 데이터
        List<SyncCongressmanDTO> dbSyncList = getDbSyncList(dbCongressmanList, idAssemblySessionMap);
        Map<String, SyncCongressmanDTO> dbSyncMap = getDbSyncMap(dbSyncList);

        List<Congressman> toInsert = new ArrayList<>();
        List<Congressman> toDelete = new ArrayList<>(dbCongressmanList);
        Map<String, Set<Integer>> codeSessionMapToInsert = new HashMap<>();

        int updateCount = 0;

        for (SyncCongressmanDTO recentSyncDTO : recentSyncList) {

            Congressman recentCongressman = recentSyncDTO.getCongressman();
            Set<Integer> recentAssemblySessions = recentSyncDTO.getAssemblySessions();

            SyncCongressmanDTO dbSyncDTO = dbSyncMap.get(recentCongressman.getCode());


            if (dbSyncDTO == null) {
                toInsert.add(recentCongressman);
                codeSessionMapToInsert.put(recentCongressman.getCode(), recentAssemblySessions);
            } else {
                Congressman dbCongressman = dbSyncDTO.getCongressman();

                Set<Integer> dbAssemblySessions = dbSyncDTO.getAssemblySessions();

                if (!equalsWithoutId(recentCongressman, dbCongressman)) {
                    // 변경감지에 의해 update 됨
                    dbCongressman.updateFieldsFrom(recentCongressman);
                    updateCount++;
                }

                if (!dbAssemblySessions.equals(recentAssemblySessions)) {
                    syncAssemblySessions(recentAssemblySessions, dbAssemblySessions, dbCongressman);
                }
                toDelete.remove(dbCongressman);
            }
        }

        List<Congressman> batchInsertResult = batchInsertCongressman(toInsert);
        int batchRemoveResultCount = batchRemoveCongressman(toDelete);
        assemblySessionService.deleteByCongressmanId(toDelete.stream().map(Congressman::getId).collect(Collectors.toList()));

        // saveAll 은 영속성 컨텍스트에 영속되지 않아서 대수 삽입 시 fk 문제 발생
        // findById 로 영속성 컨텍스트에 영속시킨 엔티티로 대수 삽입 해야한다.
        ArrayList<Congressman> insertedCongressmanList = new ArrayList<>();
        for (Congressman insertedCongressman : batchInsertResult) {
            insertedCongressmanList.add(findById(insertedCongressman.getId()));
        }

        // 대수 배치 insert 처리
        List<AssemblySession> assemblySessionsToInsert = new ArrayList<>();
        insertedCongressmanList.forEach(insertedCongressman -> setAssemblySessionsToInsert(insertedCongressman, codeSessionMapToInsert, assemblySessionsToInsert));
        List<AssemblySession> batchAssemblySessionInsertResult = assemblySessionService.saveAll(assemblySessionsToInsert);

        return CongressmanBatchResultDTO.of(batchInsertResult.stream().map(this::from).toList(), updateCount, batchRemoveResultCount);
    }

    private Map<Long, Set<Integer>> getIdAssemblySessionMap() {
        List<AssemblySession> dbAssemblySessionList = assemblySessionService.findAll();

        Map<Long, Set<Integer>> idAssemblySessionMap = new HashMap<>();
        dbAssemblySessionList.forEach(assemblySession -> {
            final Long congressmanId = assemblySession.getCongressman().getId();

            // 리스트를 가져오거나 없으면 새로 생성
            Set<Integer> sessionSet = idAssemblySessionMap.getOrDefault(congressmanId, new HashSet<>());

            // 리스트에 세션 추가
            sessionSet.add(assemblySession.getSession());

            // 맵에 리스트를 다시 넣어줌 (이미 존재하는 키라면 덮어쓰기)
            idAssemblySessionMap.put(congressmanId, sessionSet);
        });
        return idAssemblySessionMap;
    }

    private void setAssemblySessionsToInsert(Congressman insertedCongressman, Map<String, Set<Integer>> codeSessionMapToInsert, List<AssemblySession> assemblySessionsToInsert) {
        Set<Integer> sessionMapToInsert = codeSessionMapToInsert.get(insertedCongressman.getCode());
        for (Integer session : sessionMapToInsert) {
            assemblySessionsToInsert.add(AssemblySession.of(insertedCongressman, session));
        }
    }

    private List<SyncCongressmanDTO> getDbSyncList(List<Congressman> dbCongressmanList, Map<Long, Set<Integer>> idAssemblySessionMap) {
        return dbCongressmanList
                .stream()
                .map(congressman -> SyncCongressmanDTO.of(congressman, idAssemblySessionMap.get(congressman.getId()))).toList();
    }

    private Map<String, SyncCongressmanDTO> getDbSyncMap(List<SyncCongressmanDTO> dbSyncList) {
        return dbSyncList.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getCongressman().getCode(), // 키: SyncCongressmanDTO의 의원 코드
                        dto -> dto // 값: SyncCongressmanDTO 자체
                ));
    }

    private void syncAssemblySessions(Set<Integer> recentAssemblySessions, Set<Integer> dbAssemblySessions, Congressman dbCongressman) {
        HashSet<Integer> sessionsToInsert = new HashSet<>(recentAssemblySessions);
        sessionsToInsert.removeAll(dbAssemblySessions);

        HashSet<Integer> sessionsToRemove = new HashSet<>(dbAssemblySessions);
        sessionsToRemove.removeAll(recentAssemblySessions);

        assemblySessionService.saveAll(sessionsToInsert.stream().map(session -> AssemblySession.of(dbCongressman, session)).toList());

        List<AssemblySession> assemblySessionsToDelete = assemblySessionService.findAllByCongressmanIdAndSession(dbCongressman.getId(), sessionsToRemove);
        List<Long> assemblySessionIdListToDelete = assemblySessionsToDelete.stream().map(AssemblySession::getId).toList();
        assemblySessionService.deleteAllById(assemblySessionIdListToDelete);
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
                (recentCongressman.getSex() == null ? dbCongressman.getSex() == null : recentCongressman.getSex().equals(dbCongressman.getSex())) &&
                (recentCongressman.getImageUrl() == null ? dbCongressman.getImageUrl() == null : recentCongressman.getImageUrl().equals(dbCongressman.getImageUrl()));
    }
}

