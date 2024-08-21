package site.leesoyeon.avalanche.point.application.util;

 public final class Constants {

   // API
   public static final String API_VERSION = "/api/v1";
   public static final String SUCCESS_STATUS = "SUCCESS";
   public static final String ERROR_STATUS = "ERROR";
   public static final String CONTENT_TYPE_JSON = "application/json";

   // 포인트 관련 상수
   public static final int MINIMUM_POINT_THRESHOLD = 0; // 최소 포인트 잔액
   public static final int MAXIMUM_POINT_LIMIT = 1000000; // 최대 포인트 적립 한도
   public static final String POINTS_TRANSACTION_PREFIX = "POINT::TRANSACTION::";
   public static final long POINTS_CACHE_EXPIRE_TIME = 60 * 10L; // 포인트 캐시 만료 시간 10분
   public static final String POINTS_TRANSACTION_SUCCESS = "포인트 거래가 성공적으로 완료되었습니다.";
   public static final String POINTS_TRANSACTION_FAILURE = "포인트 거래에 실패했습니다.";

   // 캐시 관련 상수
   public static final String CACHE_KEY_PREFIX = "CACHE::";
   public static final long DEFAULT_CACHE_EXPIRE_TIME = 60 * 60L; // 1시간
   public static final String CACHE_USER = "USER::";

    private Constants() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
