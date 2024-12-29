package com.guenbon.siso.repository.congressman;

import com.guenbon.siso.entity.Congressman;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CongressmanRepository extends JpaRepository<Congressman, Long>, QuerydslCongressmanRepository {
    Optional<Congressman> findById(Long id);
}
