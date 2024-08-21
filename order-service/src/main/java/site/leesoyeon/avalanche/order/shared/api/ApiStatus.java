package site.leesoyeon.avalanche.order.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "요청이 성공적으로 처리되었습니다."),

    // 400 - Bad Request : 잘못된 요청
    INVALID_INPUT_VALUE(400, "IIV", "입력 값이 잘못되었습니다."),
    NOT_EXISTED_ORDER(400, "NO", "존재하지 않는 주문입니다."),
    NOT_ENOUGH_STOCK(400, "NES", "재고가 부족합니다."),

    // 401 - Unauthorized : 비인증(인증 수단이 없음)
    UNAUTHORIZED_ACCESS(401, "UA", "주문에 대한 인증이 필요합니다."),

    // 403 - Forbidden : 권한 없음 (서버가 요청을 이해했지만 승인을 거부)
    NO_PERMISSION(403, "NP", "해당 주문에 대한 권한이 없습니다."),
    FORBIDDEN_ORDER_ACCESS(403, "FOA", "주문에 접근할 수 없습니다."),

    // 404 - Not Found
    NOT_FOUND_ORDER(404, "NFO", "주문 정보를 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(404, "NFP", "존재하지 않는 상품입니다."),
    NOT_FOUND_SHIPPING_ADDRESS(404, "NFSA", "존재하지 않는 배송지입니다."),

    // 409 - Conflict
    DUPLICATE_ORDER(409, "DO", "중복된 주문입니다."),

    // 500 - Internal Server Error
    ORDER_PROCESSING_ERROR(500, "OPE", "주문 처리 중 오류가 발생했습니다."),
    PAYMENT_ERROR(500, "PE", "결제 처리 중 오류가 발생했습니다."),
    DATABASE_ERROR(500, "DBE", "데이터베이스 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(500,"ISE", "데이터베이스 연결 실패");

    private final int statusCode;
    private final String code;
    private final String message;
}
