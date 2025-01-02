package com.guenbon.siso.repository.congressman;

import com.guenbon.siso.entity.Congressman;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CongressmanRepository extends JpaRepository<Congressman, Long>, QuerydslCongressmanRepository {
    Optional<Congressman> findById(Long id);

    @Query("SELECT m.imageUrl FROM Member m JOIN Rating r ON m.id = r.member.id WHERE r.congressman.id = :congressmanId ORDER BY r.createdDate DESC limit 4")
    Optional<List<String>> getRecentMemberImagesByCongressmanId(@Param("congressmanId") Long congressmanId);

    boolean existsById(Long id);

}
