package site.leesoyeon.avalanche.shipping.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "Success"),

    // 400 - Bad Request : 잘못된 요청
    INVALID_INPUT_VALUE(400, "IIV", "입력 값이 잘못되었습니다."),
    INVALID_SHIPPING_METHOD(400, "ISM", "유효하지 않은 배송 방법입니다."),
    DUPLICATE_SHIPPING_REQUEST(400, "DSR", "중복된 배송 요청입니다."),

    // 403 - Forbidden : 권한 없음 (서버가 요청을 이해했지만 승인을 거부)
    NO_PERMISSION(403, "NP", "권한이 없습니다."),

    // 404 - Not Found : 잘못된 리소스 접근
    NOT_FOUND_SHIPPING_ADDRESS(404, "NFSA", "존재하지 않는 배송지입니다."),
    NOT_FOUND_ORDER(404, "NFO", "주문 정보를 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(404, "NFP", "존재하지 않는 상품입니다."),

    // 409 - Conflict : 중복 데이터
    CONFLICT_SHIPPING_ORDER(409, "CSO", "이미 배송이 시작된 주문입니다."),

    // 500 - Internal Server Error
    SHIPPING_SERVICE_ERROR(500, "SSE", "배송 서비스 처리 중 오류가 발생했습니다."),
    DATABASE_ERROR(500, "DBE", "데이터베이스 오류"),
    FAILED_SHIPPING_ACTION(500, "FSA", "배송 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(500,"ISE", "데이터베이스 연결 실패");

    private final int statusCode;
    private final String code;
    private final String message;
}
