package com.guenbon.siso.repository.rating;

import static com.guenbon.siso.entity.QRating.rating;

import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.entity.Rating;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@Slf4j
public class QuerydslRatingRepositoryImpl implements QuerydslRatingRepository {

    private final JPAQueryFactory jpaQueryFactory;


    public List<Rating> getSortedRatingsByCongressmanId(Long congressmanId, Pageable pageable,
                                                        DecryptedCountCursor countCursor) {
        return jpaQueryFactory.select(rating)
                .from(rating)
                .where(rating.congressman.id.eq(congressmanId), getCursorCondition(pageable, countCursor))
                .orderBy(getOrderBy(pageable), rating.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private static OrderSpecifier<Integer> getOrderBy(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if ("like".equalsIgnoreCase(property)) {
                return rating.ratingLikeList.size().desc();
            }
            if ("dislike".equalsIgnoreCase(property)) {
                return rating.ratingDisLikeList.size().desc();
            }
        }
        return rating.ratingLikeList.size().add(rating.ratingDisLikeList.size()).desc();
    }

    private static BooleanExpression getCursorCondition(Pageable pageable, DecryptedCountCursor countCursor) {
        if (countCursor == null || countCursor.isEmpty()) {
            return null;
        }

        NumberExpression<Integer> likeCount = rating.ratingLikeList.size();
        NumberExpression<Integer> disLikeCount = rating.ratingDisLikeList.size();
        NumberExpression<Integer> topicality = likeCount.add(disLikeCount);
        Integer countCursorValue = countCursor.getCountCursor();
        Long idCursorValue = countCursor.getIdCursor();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            if ("like".equalsIgnoreCase(property)) {
                return (likeCount.eq(countCursorValue).and(rating.id.loe(idCursorValue)))
                        .or(likeCount.ne(countCursorValue).and(likeCount.loe(countCursorValue)));
            }

            if ("dislike".equalsIgnoreCase(property)) {
                return (disLikeCount.eq(countCursorValue).and(rating.id.loe(idCursorValue)))
                        .or(disLikeCount.ne(countCursorValue).and(disLikeCount.loe(countCursorValue)));
            }
            if ("topicality".equalsIgnoreCase(property)) {
                return (topicality.eq(countCursorValue).and(rating.id.loe(idCursorValue)))
                        .or(topicality.ne(countCursorValue).and(topicality.loe(countCursorValue)));
            }
        }
        return null;
    }
}
