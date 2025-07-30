package site.leesoyeon.avalanche.order.application.util;

/**
 * 프로젝트 전반에서 사용되는 상수들을 정의한 클래스.
 *
 * <p>이 클래스는 API 관련, JWT 토큰, 이메일 처리, 캐시, 보안 등의 설정에 필요한 상수들을
 * 포함합니다. 모든 필드는 정적(static)이며, 인스턴스화할 수 없습니다.</p>
 */

 public final class Constants {

      // API
      public static final String API_VERSION = "/api/v1/orders";
      public static final String SUCCESS_STATUS = "SUCCESS";
      public static final String ERROR_STATUS = "ERROR";
      public static final String CONTENT_TYPE_JSON = "application/json";

      // 캐시
      public static final String CACHE_KEY_PREFIX = "CACHE::ORDERS::";
      public static final long DEFAULT_CACHE_EXPIRE_TIME = 60 * 60L; // 1시간
      public static final String CACHE_ORDER = "ORDER::";

      // 주문 관련 상수
      public static final String ORDER_ID_HEADER = "Order-Id";
      public static final String ORDER_STATUS_PENDING = "PENDING";
      public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
      public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
      public static final String PAYMENT_STATUS_PENDING = "PAYMENT_PENDING";
      public static final String PAYMENT_STATUS_COMPLETED = "PAYMENT_COMPLETED";
      public static final String PAYMENT_STATUS_FAILED = "PAYMENT_FAILED";
      public static final String SHIPPING_STATUS_PENDING = "SHIPPING_PENDING";
      public static final String SHIPPING_STATUS_SHIPPED = "SHIPPED";
      public static final String SHIPPING_STATUS_DELIVERED = "DELIVERED";

    private Constants() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
