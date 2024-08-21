package site.leesoyeon.avalanche.product.application.util;

public final class Constants {

   // API
   public static final String API_VERSION = "/api/v1";
   public static final String PRODUCT_API_BASE = API_VERSION + "/products";
   public static final String SUCCESS_STATUS = "SUCCESS";
   public static final String ERROR_STATUS = "ERROR";
   public static final String CONTENT_TYPE_JSON = "application/json";

   // Product-related
   public static final String PRODUCT_CACHE_KEY_PREFIX = "PRODUCT::";
   public static final long PRODUCT_CACHE_EXPIRE_TIME = 60 * 60 * 24L; // 24시간
   public static final int MAX_PRODUCT_NAME_LENGTH = 100;
   public static final int MIN_PRODUCT_NAME_LENGTH = 3;

   // Cache
   public static final String CACHE_KEY_PREFIX = "CACHE::";
   public static final long DEFAULT_CACHE_EXPIRE_TIME = 60 * 60L; // 1시간
   public static final String CACHE_USER = "USER::";

   private Constants() {
      throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
   }
}
