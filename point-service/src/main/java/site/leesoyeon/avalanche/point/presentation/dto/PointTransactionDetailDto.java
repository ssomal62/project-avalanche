package site.leesoyeon.avalanche.point.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import site.leesoyeon.avalanche.point.shared.enums.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포인트 트랜잭션의 상세 정보를 담는 DTO.
 *
 * @param transactionId 트랜잭션 고유 식별자
 * @param userId 사용자 고유 식별자
 * @param amount 트랜잭션 금액
 * @param balance 트랜잭션 후 잔액
 * @param activityType 트랜잭션 활동 유형
 * @param productId 관련 상품 식별자 (있는 경우)
 * @param description 트랜잭션 설명
 * @param isCancelled 트랜잭션 취소 여부
 * @param expiryDate 포인트 만료일 (있는 경우)
 * @param createdDate 트랜잭션 생성 시간
 */
public record PointTransactionDetailDto(
        @NotNull UUID transactionId,
        @NotNull UUID userId,
        @NotNull Integer amount,
        @NotNull @PositiveOrZero Integer balance,
        @NotNull ActivityType activityType,
        UUID productId,
        @Size(max = 500) String description,
        boolean isCancelled,
        LocalDate expiryDate,
        @NotNull LocalDateTime createdDate
) {
}
