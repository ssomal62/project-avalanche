package site.leesoyeon.probabilityrewardsystem.point.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포인트 트랜잭션 검색을 위한 조건을 담는 DTO.
 *
 * @param userId 검색할 사용자 ID
 * @param activityType 검색할 활동 유형
 * @param startDate 검색 시작 날짜
 * @param endDate 검색 종료 날짜
 * @param isCancelled 취소된 트랜잭션 포함 여부
 * @param minAmount 최소 트랜잭션 금액
 * @param maxAmount 최대 트랜잭션 금액
 */
public record PointTransactionSearchCondition(
        UUID userId,
        ActivityType activityType,
        @Past LocalDateTime startDate,
        @PastOrPresent LocalDateTime endDate,
        Boolean isCancelled,
        @PositiveOrZero Integer minAmount,
        @PositiveOrZero Integer maxAmount
) {
}
