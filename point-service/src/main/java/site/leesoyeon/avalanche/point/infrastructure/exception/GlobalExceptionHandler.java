package site.leesoyeon.avalanche.point.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.leesoyeon.avalanche.point.shared.api.ApiResponse;
import site.leesoyeon.avalanche.point.shared.api.ApiStatus;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PointTransactionException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(PointTransactionException ex) {
        log.error("포인트 예외 발생: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ex.getStatus(), ex.getDebugMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus().getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<site.leesoyeon.avalanche.point.shared.api.ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("예기치 않은 오류 발생: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ApiStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

