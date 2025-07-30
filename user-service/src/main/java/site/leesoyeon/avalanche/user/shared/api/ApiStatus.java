package site.leesoyeon.avalanche.user.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "Success"),

    // 400 - Bad Request
    VALIDATION_FAILED(400, "VF", "유효성 검사가 실패하였습니다."),
    DUPLICATE_EMAIL(400, "DE", "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(400, "DN", "중복된 닉네임입니다."),
    DUPLICATE_TEL_NUMBER(400, "DT", "중복된 전화번호입니다."),
    NOT_EXISTED_USER(400, "NU", "존재하지 않는 사용자입니다."),
    INVALID_INPUT_VALUE(400, "IIV", "입력 값이 잘못되었습니다."),
    INVALID_USER_ROLE(400, "IUR", "존재하지 않는 권한입니다."),
    SAME_AS_OLD_PASSWORD(400, "SAOP", "새 비밀번호는 기존 비밀번호와 동일할 수 없습니다."),

    // 403 - Forbidden
    RESTRICTED_USER(403, "RA", "이용 제한된 사용자입니다."),
    PROHIBITED_USERNAME(403, "PU", "사용할 수 없는 이름입니다."),
    NO_PERMISSION(403, "NP", "권한이 없습니다."),
    NO_AUTHORIZATION(403, "NP", "유효한 인증 정보가 없습니다."),

    // 404 - Not Found
    NOT_FOUND_USER(404, "NFA", "존재하지 않는 사용자입니다."),

    // 409 - Conflict
    CONFLICT_ACCOUNT(409, "CA", "이미 가입한 계정입니다."),
    DELETED_ACCOUNT(409, "DA", "이미 탈퇴한 계정입니다."),
    POINT_TRANSACTION_FAILED(409, "PTF", "포인트 거래 처리에 실패했습니다."),

    // 500 - Internal Server Error
    INTERNAL_SERVER_ERROR(500,"ISE", "데이터베이스 연결 실패");

    private final int statusCode;
    private final String code;
    private final String message;
}