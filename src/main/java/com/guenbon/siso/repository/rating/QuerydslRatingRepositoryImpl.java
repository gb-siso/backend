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

    public static final String SORT_LIKE = "like";
    public static final String SORT_DISLIKE = "dislike";
    public static final String SORT_TOPICALITY = "topicality";

    private final JPAQueryFactory jpaQueryFactory;

    public List<Rating> getSortedRatingsByCongressmanId(final Long congressmanId, final Pageable pageable,
                                                        final DecryptedCountCursor countCursor) {
        return jpaQueryFactory.select(rating)
                .from(rating)
                .where(rating.congressman.id.eq(congressmanId), getCursorCondition(pageable, countCursor))
                .orderBy(getOrderBy(pageable), rating.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private static OrderSpecifier<Integer> getOrderBy(final Pageable pageable) {
        return pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .filter(sortProperty -> sortProperty != null)
                .map(QuerydslRatingRepositoryImpl::getOrderSpecifier)
                .findFirst()
                .orElseGet(QuerydslRatingRepositoryImpl::getDefaultOrderSpecifier);
    }

    private static OrderSpecifier<Integer> getDefaultOrderSpecifier() {
        return rating.ratingLikeList.size().add(rating.ratingDislikeList.size()).desc();
    }

    private static OrderSpecifier<Integer> getOrderSpecifier(final String sortProperty) {
        if (SORT_LIKE.equalsIgnoreCase(sortProperty)) {
            return rating.ratingLikeList.size().desc();
        }
        if (SORT_DISLIKE.equalsIgnoreCase(sortProperty)) {
            return rating.ratingDislikeList.size().desc();
        }
        return getDefaultOrderSpecifier();
    }

    private static BooleanExpression getCursorCondition(final Pageable pageable,
                                                        final DecryptedCountCursor countCursor) {
        if (countCursor == null || countCursor.isEmpty()) {
            return null;
        }

        return pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .filter(sortProperty -> sortProperty != null)
                .map(sortProperty -> getCursorExpression(sortProperty, countCursor))
                .findFirst()
                .orElse(null);
    }

    private static BooleanExpression getCursorExpression(final String sortProperty,
                                                         final DecryptedCountCursor countCursor) {
        final NumberExpression<Integer> likeCount = rating.ratingLikeList.size();
        final NumberExpression<Integer> disLikeCount = rating.ratingDislikeList.size();
        final NumberExpression<Integer> topicality = likeCount.add(disLikeCount);
        final Integer countCursorValue = countCursor.getCountCursor();
        final Long idCursorValue = countCursor.getIdCursor();

        if (SORT_LIKE.equalsIgnoreCase(sortProperty)) {
            return likeCount.eq(countCursorValue).and(rating.id.loe(idCursorValue))
                    .or(likeCount.ne(countCursorValue).and(likeCount.loe(countCursorValue)));
        }
        if (SORT_DISLIKE.equalsIgnoreCase(sortProperty)) {
            return disLikeCount.eq(countCursorValue).and(rating.id.loe(idCursorValue))
                    .or(disLikeCount.ne(countCursorValue).and(disLikeCount.loe(countCursorValue)));
        }
        if (SORT_TOPICALITY.equalsIgnoreCase(sortProperty)) {
            return topicality.eq(countCursorValue).and(rating.id.loe(idCursorValue))
                    .or(topicality.ne(countCursorValue).and(topicality.loe(countCursorValue)));
        }
        return null;
    }
}