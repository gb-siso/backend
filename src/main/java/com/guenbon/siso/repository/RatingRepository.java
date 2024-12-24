package com.guenbon.siso.repository;

import com.guenbon.siso.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
