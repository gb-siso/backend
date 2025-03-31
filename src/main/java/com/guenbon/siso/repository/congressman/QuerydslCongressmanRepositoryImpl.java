package com.guenbon.siso.repository.congressman;

import com.guenbon.siso.dto.congressman.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.projection.CongressmanListProjectionDTO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.guenbon.siso.entity.QRating.rating;
import static com.guenbon.siso.entity.congressman.QAssemblySession.assemblySession;
import static com.guenbon.siso.entity.congressman.QCongressman.congressman;

@RequiredArgsConstructor
public class QuerydslCongressmanRepositoryImpl implements QuerydslCongressmanRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CongressmanGetListDTO> getList(Pageable pageable, Long cursorId, Double cursorRating, String party,
                                               String search) {
        List<CongressmanListProjectionDTO> fetch = jpaQueryFactory.select(
                        Projections.fields(
                                CongressmanListProjectionDTO.class,
                                congressman.id,
                                congressman.name,
                                congressman.timesElected,
                                congressman.party,
                                congressman.code,
                                congressman.position,
                                congressman.electoralDistrict,
                                congressman.electoralType,
                                congressman.sex,
                                congressman.imageUrl,
                                rating.rate.avg().as("rate"),
                                Expressions.stringTemplate("GROUP_CONCAT({0})", assemblySession.session)
                                        .as("assemblySessions")  // 문자열로 변환 후 DTO에서 파싱 필요
                        ))
                .from(congressman)
                .leftJoin(rating)
                .on(congressman.id.eq(rating.congressman.id))
                .leftJoin(assemblySession)
                .on(congressman.id.eq(assemblySession.congressman.id))
                .groupBy(congressman.id)
                .distinct()
                .having(cursorCondition(cursorId, cursorRating, pageable))
                .where(party != null ? congressman.party.eq(party) : null)
                .where(search != null ? congressman.name.like("%" + search + "%") : null)
                .orderBy(createOrderBy(pageable))
                .limit(pageable.getPageSize())
                .fetch();

        return new ArrayList<>(fetch.stream().map(CongressmanGetListDTO::from).toList());
    }

    private BooleanExpression cursorCondition(Long cursorId, Double cursorRating, Pageable pageable) {
        if (cursorId == Long.MAX_VALUE) {
            return null; // 첫 페이지
        }

        boolean isDescending = pageable.getSort().getOrderFor("rate").isDescending();
        if (cursorRating != null) {
            return rating.rate.avg().eq(cursorRating).and(congressman.id.goe(cursorId))
                    .or(
                            rating.rate.avg().ne(cursorRating)
                                    .and(isDescending ? rating.rate.avg().loe(cursorRating)
                                            : rating.rate.avg().goe(cursorRating)));
        }
        // 마지막 원소 rating 값이 null 이라는 뜻은 rating 있는 국회의원은 이미 다 표시했다는 뜻. 즉 id 기준으로만 정렬해야함
        return congressman.id.goe(cursorId).and(rating.rate.avg().isNull());
    }

    private OrderSpecifier<?>[] createOrderBy(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>(
                List.of(pageable.getSort().getOrderFor("rate").isDescending()
                                ? rating.rate.avg().desc()
                                : rating.rate.avg().asc(),
                        congressman.id.asc()
                )
        );
        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
    }
}
