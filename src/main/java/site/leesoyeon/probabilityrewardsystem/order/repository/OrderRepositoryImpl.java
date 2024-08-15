package site.leesoyeon.probabilityrewardsystem.order.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderSearchCondition;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.entity.QOrder;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findOrdersWithCondition(OrderSearchCondition condition, Pageable pageable) {
        QOrder order = QOrder.order;

        List<Order> content = queryFactory
                .selectFrom(order)
                .where(
                        userIdEq(condition.userId()),
                        statusEq(condition.status())
                )
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(order)
                .where(
                        userIdEq(condition.userId()),
                        statusEq(condition.status())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression userIdEq(UUID userId) {
        return userId != null ? QOrder.order.userId.eq(userId) : null;
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? QOrder.order.status.eq(status) : null;
    }
}
