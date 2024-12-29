package com.guenbon.siso.repository.congressman;

import static com.guenbon.siso.entity.QCongressman.congressman;
import static com.guenbon.siso.entity.QRating.rating;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class QuerydslCongressmanRepositoryImpl implements QuerydslCongressmanRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CongressmanGetListDTO> getList(Pageable pageable, Long cursorId, Double cursorRating, String party,
                                               String search) {
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
                .having(cursorCondition(cursorId, cursorRating, pageable))
                .where(party != null ? congressman.party.eq(party) : null)
                .where(search != null ? congressman.name.like("%" + search + "%") : null)
                // rating에 따라 정렬, rating 같을 경우 id 낮은 순
                .orderBy(createOrderBy(pageable))
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return fetch;
    }

    private BooleanExpression cursorCondition(Long cursorId, Double cursorRating, Pageable pageable) {
        if (cursorId == Long.MAX_VALUE) {
            return null; // 첫 페이지
        }
        boolean isDescending = pageable.getSort().getOrderFor("rating").isDescending();
        return rating.rate.avg().eq(cursorRating).and(congressman.id.goe(cursorId))
                .or(
                        rating.rate.avg().ne(cursorRating)
                                .and(isDescending ? rating.rate.avg().loe(cursorRating)
                                        : rating.rate.avg().goe(cursorRating)));
    }

    private OrderSpecifier<?>[] createOrderBy(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>(
                List.of(pageable.getSort().getOrderFor("rating").isDescending()
                                ? rating.rate.avg().desc()
                                : rating.rate.avg().asc(),
                        congressman.id.asc()
                )
        );
        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
    }
}
