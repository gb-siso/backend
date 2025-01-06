package com.guenbon.siso.repository.rating;

import static com.guenbon.siso.entity.QRating.rating;

import com.guenbon.siso.entity.Rating;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class QuerydslRatingRepositoryImpl implements QuerydslRatingRepository {

    private final JPAQueryFactory jpaQueryFactory;


    public List<Rating> getRecentRatingByCongressmanId(Long congressmanId, Pageable pageable) {
        return jpaQueryFactory.select(rating)
                .from(rating)
                .where(rating.congressman.id.eq(congressmanId))
                .orderBy(getOrderBy(pageable), rating.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private static OrderSpecifier<Integer> getOrderBy(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty(); // 정렬 기준 속성
            if ("like".equalsIgnoreCase(property)) {
                return rating.ratingLikeList.size().desc();
            }
            if ("dislike".equalsIgnoreCase(property)) {
                return rating.ratingDisLikeList.size().desc();
            }
        }
        return rating.ratingLikeList.size().add(rating.ratingDisLikeList.size()).desc();
    }
}
