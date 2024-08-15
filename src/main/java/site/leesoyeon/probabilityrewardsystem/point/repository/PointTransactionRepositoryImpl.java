package site.leesoyeon.probabilityrewardsystem.point.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionSearchCondition;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionSummaryDto;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;
import site.leesoyeon.probabilityrewardsystem.point.entity.QPointTransaction;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PointTransactionRepositoryImpl implements PointTransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;

    @Override
    public Page<PointTransactionSummaryDto> findAllByUserId(UUID userId, Pageable pageable) {
        List<PointTransactionSummaryDto> content = queryFactory
                .select(Projections.constructor(
                        PointTransactionSummaryDto.class,
                        qPointTransaction.activityType,
                        qPointTransaction.amount,
                        qPointTransaction.balance,
                        qPointTransaction.productId,
                        qPointTransaction.description,
                        qPointTransaction.expiryDate,
                        qPointTransaction.createdDate
                ))
                .from(qPointTransaction)
                .where(userIdEq(userId))
                .orderBy(qPointTransaction.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(qPointTransaction.count())
                .from(qPointTransaction)
                .where(userIdEq(userId))
                .fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<PointTransaction> findPointTransactionWithCondition(PointTransactionSearchCondition condition) {
        return queryFactory
                .selectFrom(qPointTransaction)
                .where(
                    userIdEq(condition.userId()),
                        activityTypeEq(condition.activityType()),
                        dateBetween(condition.startDate(), condition.endDate()),
                        isCancelledEq(condition.isCancelled()),
                        amountBetween(condition.minAmount(), condition.maxAmount())
                )
                .orderBy(qPointTransaction.createdDate.desc())
                .fetch();
    }

//  ============================================
//              Condition Builders
//  ============================================

    private BooleanExpression userIdEq(UUID userId) {
        return userId != null ? QPointTransaction.pointTransaction.userId.eq(userId) : null;
    }

    private BooleanExpression activityTypeEq(ActivityType activityType) {
        return activityType != null ? QPointTransaction.pointTransaction.activityType.eq(activityType) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate != null && endDate != null) {
            return QPointTransaction.pointTransaction.createdDate.between(startDate, endDate);
        } else if (startDate != null) {
            return QPointTransaction.pointTransaction.createdDate.goe(startDate);
        } else if (endDate != null) {
            return QPointTransaction.pointTransaction.createdDate.loe(endDate);
        } else {
            return null;
        }
    }

    private BooleanExpression isCancelledEq(Boolean isCancelled) {
        return isCancelled != null ? QPointTransaction.pointTransaction.isCancelled.eq(isCancelled) : null;
    }

    private BooleanExpression amountBetween(Integer minAmount, Integer maxAmount) {
        if(minAmount != null && maxAmount != null) {
            return QPointTransaction.pointTransaction.amount.between(minAmount, maxAmount);
        } else if (minAmount != null) {
            return QPointTransaction.pointTransaction.amount.goe(minAmount);
        } else if (maxAmount != null) {
            return QPointTransaction.pointTransaction.amount.loe(maxAmount);
        } else {
            return null;
        }
    }
}
