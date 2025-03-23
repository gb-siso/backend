package com.guenbon.siso.service.assemblysession;

import com.guenbon.siso.entity.congressman.AssemblySession;
import com.guenbon.siso.repository.assemblysession.AssemblySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AssemblySessionService {

    private final AssemblySessionRepository assemblySessionRepository;

    @Transactional(readOnly = false)
    public void deleteByCongressmanId(List<Long> congressmanIdList) {
        assemblySessionRepository.batchDeleteByCongressmanIdList(congressmanIdList);
    }

    @Transactional(readOnly = false)
    public List<AssemblySession> saveAll(List<AssemblySession> list) {
        return assemblySessionRepository.saveAll(list);
    }

    public List<AssemblySession> findAllByCongressmanIdAndSession(Long dbCongressmanId, Set<Integer> sessionList) {
        return assemblySessionRepository.findAllByCongressmanIdAndSessionIn(dbCongressmanId, sessionList);
    }

    public List<AssemblySession> findAll() {
        return assemblySessionRepository.findAll();
    }

    @Transactional(readOnly = false)
    public void deleteAllById(List<Long> list) {
        assemblySessionRepository.batchDelete(list);
    }
}
