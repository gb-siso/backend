package com.guenbon.siso.repository.rating;

import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.CustomException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.guenbon.siso.entity.QRating.rating;
import static com.guenbon.siso.exception.errorCode.PageableErrorCode.UNSUPPORTED_SORT_PROPERTY;

@RequiredArgsConstructor
@Slf4j
public class QuerydslRatingRepositoryImpl implements QuerydslRatingRepository {

    public static final String SORT_LIKE = "like";
    public static final String SORT_DISLIKE = "dislike";
    public static final String SORT_TOPICALITY = "topicality";
    public static final String SORT_REG_DATE = "regDate";

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

    private static OrderSpecifier<?> getOrderBy(final Pageable pageable) {
        return pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .filter(sortProperty -> sortProperty != null)
                .map(QuerydslRatingRepositoryImpl::getOrderSpecifier)
                .findFirst()
                .orElseThrow(() -> new CustomException(UNSUPPORTED_SORT_PROPERTY));
    }

    private static OrderSpecifier<?> getDefaultOrderSpecifier() {
        return rating.ratingLikeList.size().add(rating.ratingDislikeList.size()).desc();
    }

    private static OrderSpecifier<?> getOrderSpecifier(final String sortProperty) {
        if (SORT_LIKE.equalsIgnoreCase(sortProperty)) {
            return rating.ratingLikeList.size().desc();
        }
        if (SORT_DISLIKE.equalsIgnoreCase(sortProperty)) {
            return rating.ratingDislikeList.size().desc();
        }
        if (SORT_REG_DATE.equalsIgnoreCase(sortProperty)) {
            return rating.id.desc();
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
        final NumberPath<Long> id = rating.id;
        final Integer countCursorValue = countCursor.getCountCursor();
        final Long idCursorValue = countCursor.getIdCursor();

        if (SORT_LIKE.equalsIgnoreCase(sortProperty)) {
            return likeCount.eq(countCursorValue).and(id.loe(idCursorValue))
                    .or(likeCount.ne(countCursorValue).and(likeCount.loe(countCursorValue)));
        }
        if (SORT_DISLIKE.equalsIgnoreCase(sortProperty)) {
            return disLikeCount.eq(countCursorValue).and(id.loe(idCursorValue))
                    .or(disLikeCount.ne(countCursorValue).and(disLikeCount.loe(countCursorValue)));
        }
        if (SORT_TOPICALITY.equalsIgnoreCase(sortProperty)) {
            return topicality.eq(countCursorValue).and(id.loe(idCursorValue))
                    .or(topicality.ne(countCursorValue).and(topicality.loe(countCursorValue)));
        }
        if (SORT_REG_DATE.equalsIgnoreCase(sortProperty)) {
            return id.loe(idCursorValue);
        }
        return null;
    }
}