package com.guenbon.siso.repository;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CongressmanRepository extends JpaRepository<Congressman, Long> {
}
