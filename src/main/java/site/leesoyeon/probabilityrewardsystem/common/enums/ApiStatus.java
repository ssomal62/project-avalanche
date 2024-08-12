package site.leesoyeon.probabilityrewardsystem.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "Success"),

    // 400 - Bad Request : 잘못된 요청
    VALIDATION_FAILED(400, "VF", "유효성 검사가 실패하였습니다."),
    DUPLICATE_EMAIL(400, "DE", "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(400, "DN", "중복된 닉네임입니다."),
    DUPLICATE_TEL_NUMBER(400, "DT", "중복된 전화번호입니다."),
    NOT_EXISTED_USER(400, "NU", "존재하지 않는 사용자입니다."),
    NOT_EXISTED_CONTENT(400, "NB", "존재하지 않는 게시물입니다."),
    INVALID_INPUT_VALUE(400, "IIV", "입력 값이 잘못되었습니다."),
    INVALID_TYPE_VALUE(400, "ITV", "요청 타입이 잘못되었습니다."),
    INVALID_USER_ROLE(400, "IUR", "존재하지 않는 권한입니다."),
    BLACKLISTED_IP(400, "BIP", "차단된 IP입니다."),
    NO_FILE_TO_UPLOAD(400, "NFTU", "업로드할 파일이 없습니다."),
    NOT_ALLOWED_EMPTY_CONTENT(400, "NAEC", "내용을 입력해주세요."),
    NOT_ALLOWED_EMPTY_SUBJECT(400, "NAES", "제목을 입력해주세요."),
    NOT_ALLOWED_EMPTY_CATEGORY(400, "NAEC", "카테고리를 입력해주세요."),
    NOT_ALLOWED_EMPTY_DETAIL_CATEGORY(400, "NAEDC", "상세카테고리를 입력해주세요."),
    NOT_ALLOWED_EMPTY_DATE(400, "NAED", "날짜를 입력해주세요."),
    DEFAULT_ADDRESS_ALREADY_EXISTS(400, "DAAE", "이미 등록된 기본 주소가 있습니다."),
    SAME_AS_OLD_PASSWORD(400, "SAOP", "새 비밀번호는 기존 비밀번호와 동일할 수 없습니다."),
    CLIENT_ID_NULL_OR_EMPTY(400, "CINOE", "클라이언트 ID는 null이거나 비어 있을 수 없습니다."),

    // 401 - Unauthorized : 비인증(인증 수단이 없음)
    SIGN_IN_FAIL(401, "SF", "로그인 실패하였습니다."),
    AUTHORIZATION_FAIL(401, "AF", "인증 실패하였습니다."),
    NO_AUTHORIZATION(401, "NA", "인증 정보가 없습니다."),
    INVALID_ID_OR_PW(401, "IIP", "아이디 혹은 비밀번호가 틀렸습니다."),
    INVALID_AUTH_ERROR(401, "IAE", "지원 되지 않거나 잘못된 인증 수단입니다."),
    INVALID_EMAIL_VERIFICATION_CODE(401, "IEVC", "이메일 인증 코드가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "RTNF", "리프레시 토큰을 찾을 수 없습니다."),
    INVALID_JWT_SIGNATURE(401, "IJS", "유효하지 않은 JWT 서명입니다."),
    UNSUPPORTED_JWT_TOKEN(401, "UJT", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(401, "IJT", "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(401, "EJT", "만료된 JWT 토큰입니다."),

    // 403 - Forbidden : 권한 없음 (서버가 요청을 이해했지만 승인을 거부)
    NOT_POST_OWNER(403, "NPO", "해당 게시글의 소유자가 아닙니다."),
    RESTRICTED_ACCOUNT(403, "RA", "이용 제한된 계정입니다."),
    PROHIBITED_USERNAME(403, "PU", "사용할 수 없는 이름입니다."),
    EMAIL_ALREADY_VERIFIED(403, "EAV", "이미 인증된 이메일입니다."),
    EMAIL_ALREADY_SENT(403, "EAS", "이미 이메일이 전송되었습니다. 3분 후에 다시 시도해주세요."),
    BLACKLISTED_TOKEN(403, "BT", "무효화된 토큰"),
    EXPIRED_TOKEN(403, "ET", "만료된 토큰"),
    INVALID_TOKEN(403, "IT", "잘못된 토큰"),
    EXPIRED_REFRESH_TOKEN(403, "ERT", "리프레시 토큰 만료"),
    NO_PERMISSION(403, "NP", "권한이 없습니다."),

    // 404 - Not Found : 잘못된 리소스 접근
    NOT_FOUND_USER(404, "NFA", "존재하지 않는 계정입니다."),
    NOT_FOUND_POST(404, "NFP", "게시글이 존재하지 않습니다."),
    NOT_FOUND_FILE(404, "NFF", "파일이 존재하지 않습니다."),
    NOT_FOUND_REDIS_KEY(404, "NFRK", "존재하지 않는 REDIS KEY 입니다."),
    NOT_FOUND_PRODUCT(404, "NFR", " 존재하지 않는 상품입니다."),

    // 405 - Method Not Allowed
    METHOD_NOT_ALLOWED(405, "MNA", "허용되지 않는 HTTP 메서드입니다."),

    // 409 - Conflict : 중복 데이터
    CONFLICT_ACCOUNT(409, "CA", "이미 가입한 계정입니다."),
    DUPLICATED_CATEGORY(409, "DC", "이미 등록된 카테고리 입니다."),
    LOCAL_ACCOUNT_ALREADY_EXIST(409, "LAE", "이미 등록된 계정입니다."),
    DELETED_ACCOUNT(409, "DA", "이미 탈퇴한 계정입니다."),

    // 413 - Payload Too Large
    TOO_MANY_FILES(413, "TMF", "파일은 최대 3개만 업로드 할 수 있습니다."),
    FILE_TOO_LARGE(413, "FTL", "파일은 최대 3MB까지 업로드 할 수 있습니다."),

    // 415 - Unsupported Media Type
    UNSUPPORTED_FILE_FORMAT(415, "UFF", "지원하지 않는 파일 형식입니다."),

    // 429 - Too Many Requests
    TOO_MANY_REQUESTS(429, "TMR", "요청 횟수를 초과하였습니다. 잠시 후 다시 시도해주세요."),

    // 500 - Internal Server Error
    DATABASE_ERROR(500, "DBE", "데이터베이스 오류"),
    FAIL_TO_SEND_EMAIL(500, "FTSE", "이메일 전송 실패"),
    FAILED_TO_UPLOAD_FILE(500, "FTUF", "파일 업로드 실패"),
    FAILED_HTTP_ACTION(500, "FHA", "HTTP 요청 실패"),
    EMAIL_SEND_FAILURE(500, "ESF", "이메일 전송 중 오류가 발생했습니다."),
    EMAIL_VERIFICATION_FAILURE(500, "EVF", "이메일 인증 중 오류가 발생했습니다."),
    SOCKET_TIMEOUT(500, "STO", "소켓 타임아웃 오류로 인해 이메일 전송에 실패했습니다.");

    private final int statusCode;
    private final String code;
    private final String message;
}
