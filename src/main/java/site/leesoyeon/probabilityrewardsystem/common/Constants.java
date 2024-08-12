package site.leesoyeon.probabilityrewardsystem.common;

/**
 * 프로젝트 전반에서 사용되는 상수들을 정의한 클래스.
 *
 * <p>이 클래스는 API 관련, JWT 토큰, 이메일 처리, 캐시, 보안 등의 설정에 필요한 상수들을
 * 포함합니다. 모든 필드는 정적(static)이며, 인스턴스화할 수 없습니다.</p>
 */

 public final class Constants {

    // API
    public static final String API_VERSION = "/api/v1";
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String ERROR_STATUS = "ERROR";
    public static final String CONTENT_TYPE_JSON = "application/json";

    //JWT
    public static final String BLACK_LIST_KEY_PREFIX = "JWT::BLACK_LIST::";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7 * 2; // 일단 2주로함 개발환경에서 불편하지 않게
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7 * 2; // 2주

    //EMAIL
    public static final String PREFIX_VERIFY = "EMAIL::VERIFY::";
    public static final String PREFIX_VERIFIED = "EMAIL::VERIFIED::";
    public static final String PREFIX_PW_RESET = "EMAIL::PW_RESET::";
    public static final String VALUE_TRUE = "TRUE";
    public static final String ENCODING_CHARSET = "UTF-8";
    public static final String MAILER_SUBTYPE = "HTML";
    public static final int VERIFICATION_CODE_LENGTH = 6;
    public static final Long VERIFICATION_CODE_EXPIRE_TIME = 60 * 3L; // 3분
    public static final Long EMAIL_VERIFICATION_TOKEN_EXPIRE_TIME = 3600 * 1000L;
    public static final Long WHITE_LIST_VERIFIED_TIME = 60 * 60 * 24L; // 1일

    // Client
    public static final String CLIENT_ID_HEADER = "Client-Id";

    // 캐시
    public static final String CACHE_KEY_PREFIX = "CACHE::";
    public static final long DEFAULT_CACHE_EXPIRE_TIME = 60 * 60L; // 1시간
    public static final String CACHE_USER = "USER::";

    // 보안 관련 상수
    public static final String PASSWORD_SALT = "SOME_RANDOM_SALT";
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;

    private Constants() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
