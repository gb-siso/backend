package com.guenbon.siso.repository.congressman;

import com.guenbon.siso.entity.congressman.Congressman;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CongressmanRepository extends JpaRepository<Congressman, Long>, QuerydslCongressmanRepository {
    Optional<Congressman> findById(Long id);

    @Query("SELECT m.imageUrl FROM Member m JOIN Rating r ON m.id = r.member.id WHERE r.congressman.id = :congressmanId ORDER BY r.createdDate DESC limit 4")
    List<String> getRecentMemberImagesByCongressmanId(@Param("congressmanId") Long congressmanId);

    boolean existsById(Long id);

    @Override
    @NonNull
    List<Congressman> findAll();

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Congressman c WHERE c.id IN :idList")
    int batchDelete(@Param("idList") List<Long> idList);

    Optional<Congressman> findByName(String name);
}
