package site.leesoyeon.probabilityrewardsystem.point.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.probabilityrewardsystem.point.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionDetailDto;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionListDto;
import site.leesoyeon.probabilityrewardsystem.point.service.PointTransactionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointTransactionController {

    private final PointTransactionService pointTransactionService;

    /**
     * 특정 포인트 거래의 세부 정보를 조회합니다.
     *
     * @param transactionId 조회할 거래의 고유 식별자
     * @return 포인트 거래의 세부 정보를 담은 {@link ResponseEntity}
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<PointTransactionDetailDto> getPointTransaction(
            @PathVariable(value = "transactionId") UUID transactionId) {
        PointTransactionDetailDto transactionDetail = pointTransactionService.getPointTransactionDetail(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionDetail);
    }

    /**
     * 특정 사용자의 포인트 거래 목록을 페이지네이션하여 조회합니다.
     *
     * @param userId 조회할 사용자의 고유 식별자
     * @param pageable 페이지네이션 정보
     * @return 사용자의 포인트 거래 목록을 담은 {@link ResponseEntity}
     */
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<PointTransactionListDto> getUserPointTransactions(
            @PathVariable UUID userId, Pageable pageable) {
        PointTransactionListDto pointTransactionListDto = pointTransactionService.getPointTransactions(userId, pageable);
        return ResponseEntity.ok(pointTransactionListDto);
    }

    /**
     * 사용자의 포인트를 수동으로 조정합니다.
     *
     * @param request 포인트 조정 요청 객체
     * @return 상태 코드를 담은 {@link ResponseEntity}
     */
    @PostMapping("/adjust")
    public ResponseEntity<Void> adjustPointsManually(@RequestBody @Valid ManualPointAdjustmentRequest request) {
        pointTransactionService.adjustPointsManually(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
