package site.leesoyeon.avalanche.point.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "Success"),

    // 400 - Bad Request : 잘못된 요청
    INSUFFICIENT_POINTS(400, "IP", "포인트가 부족합니다."),
    INVALID_POINT_AMOUNT(400, "IPA", "유효하지 않은 포인트 금액입니다."),

    // 404 - Not Found : 잘못된 리소스 접근
    NOT_FOUND_POINT_HISTORY(404, "NFPH", "포인트 내역을 찾을 수 없습니다."),

    // 409 - Conflict : 중복 데이터
    POINT_TRANSACTION_FAILED(409, "PTF", "포인트 거래 처리에 실패했습니다."),

    // 500 - Internal Server Error
    POINT_SYSTEM_ERROR(500, "PSE", "포인트 시스템 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(500,"ISE", "데이터베이스 연결 실패");

    private final int statusCode;
    private final String code;
    private final String message;
}
