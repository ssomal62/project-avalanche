package site.leesoyeon.avalanche.order.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.leesoyeon.avalanche.order.shared.api.ApiResponse;
import site.leesoyeon.avalanche.order.shared.api.ApiStatus;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(OrderException ex) {
        log.error("주문 예외 발생: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ex.getStatus(), ex.getDebugMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus().getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("예기치 않은 오류 발생: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ApiStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

