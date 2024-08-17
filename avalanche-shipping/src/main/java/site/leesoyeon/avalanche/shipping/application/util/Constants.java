package site.leesoyeon.avalanche.shipping.application.util;

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

   // 캐시
   public static final String CACHE_KEY_PREFIX = "CACHE::SHIPPING::";
   public static final long DEFAULT_CACHE_EXPIRE_TIME = 60 * 60L; // 1시간
   public static final String CACHE_SHIPPING_ORDER = "SHIPPING_ORDER::";

   // 배송 관련 상수
   public static final String SHIPPING_ORDER_ID_HEADER = "Shipping-Order-Id";
   public static final String SHIPPING_STATUS_PENDING = "PENDING";
   public static final String SHIPPING_STATUS_IN_PROGRESS = "IN_PROGRESS";
   public static final String SHIPPING_STATUS_COMPLETED = "COMPLETED";
   public static final String SHIPPING_STATUS_CANCELLED = "CANCELLED";

   public static final String SHIPPING_METHOD_STANDARD = "STANDARD";
   public static final String SHIPPING_METHOD_EXPRESS = "EXPRESS";
   public static final String SHIPPING_METHOD_OVERNIGHT = "OVERNIGHT";

   public static final String SHIPPING_STARTED_MSG = "배송이 시작되었습니다.";
   public static final String SHIPPING_COMPLETED_MSG = "배송이 완료되었습니다.";
   public static final String SHIPPING_CANCELLED_MSG = "배송이 취소되었습니다.";

    private Constants() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
