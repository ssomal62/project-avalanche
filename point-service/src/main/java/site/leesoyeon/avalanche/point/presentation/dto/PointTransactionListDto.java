package site.leesoyeon.avalanche.point.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

/**
 * 페이지네이션된 포인트 트랜잭션 목록을 담는 DTO.
 *
 * @param transactions 페이지네이션된 트랜잭션 상세 정보 목록
 */
public record PointTransactionListDto(
        @NotNull @Valid Page<PointTransactionSummaryDto> transactions
) {
}
