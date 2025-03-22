package com.guenbon.siso.repository.assemblysession;

import com.guenbon.siso.entity.congressman.AssemblySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AssemblySessionRepository extends JpaRepository<AssemblySession, Long> {

    List<AssemblySession> findAllByCongressmanIdAndSessionIn(Long congressmanId, Set<Integer> sessions);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AssemblySession a WHERE a.id IN :idList")
    void batchDelete(List<Long> idList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AssemblySession a WHERE a.congressman.id IN :congressmanIdList")
    void batchDeleteByCongressmanIdList(List<Long> congressmanIdList);
}

