package com.guenbon.siso.repository.congressman;

import static com.guenbon.siso.entity.QCongressman.congressman;
import static com.guenbon.siso.entity.QRating.rating;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class QuerydslCongressmanRepositoryImpl implements QuerydslCongressmanRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CongressmanGetListDTO> getList(Pageable pageable, Long cursorId, Double cursorRating) {
        List<CongressmanGetListDTO> fetch = jpaQueryFactory.select(
                        Projections.constructor(
                                CongressmanGetListDTO.class,
                                congressman.id,
                                congressman.name,
                                rating.rate.avg().as("rate")))
                .from(congressman)
                .join(rating)
                .on(congressman.id.eq(rating.congressman.id))
                .groupBy(congressman.id)
                .distinct()
                .having(cursorCondition(cursorId, cursorRating))
                // rating 높은 순, rating 같을 경우 id 낮은 순
                .orderBy(rating.rate.avg().desc(), congressman.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return fetch;
    }

    private BooleanExpression cursorCondition(Long cursorId, Double cursorRating) {
        if (cursorId == Long.MAX_VALUE) {
            return null; // 첫 페이지
        }
        return rating.rate.avg().loe(cursorRating) // rating이 낮은 것
                .or(rating.rate.avg().eq(cursorRating)
                        .and(congressman.id.gt(cursorId))); // 동일 rating일 경우 ID를 기준으로 필터
    }
}
