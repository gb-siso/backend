package com.guenbon.siso.repository.rating;

import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long>, QuerydslRatingRepository {
    @Query("SELECT r FROM Rating r WHERE r.member.id = :memberId AND r.congressman.id = :congressmanId")
    Optional<Rating> findByMemberIdAndCongressmanId(@Param("memberId") Long memberId,
                                                    @Param("congressmanId") Long congressmanId);

    boolean existsByMemberAndCongressman(Member member, Congressman congressman);
}
