package site.leesoyeon.avalanche.user.application.util;

 public final class Constants {

   // API
   public static final String CONTENT_TYPE_JSON = "application/json";

   // 유저 관련 상수
   public static final String USER_DEFAULT_ROLE = "ROLE_USER";
   public static final int USER_PASSWORD_MIN_LENGTH = 8;
   public static final int USER_PASSWORD_MAX_LENGTH = 20;
   public static final int USER_NICKNAME_MIN_LENGTH = 2;
   public static final int USER_NICKNAME_MAX_LENGTH = 15;
   public static final int USER_PHONE_NUMBER_LENGTH = 11;
   public static final int USER_EMAIL_MAX_LENGTH = 50;

   // 유저 상태 관련
   public static final String USER_STATUS_ACTIVE = "활성화";
   public static final String USER_STATUS_INACTIVE = "비활성화";
   public static final String USER_STATUS_DELETED = "탈퇴";

   // 유저 캐시 관련
   public static final String CACHE_USER_KEY_PREFIX = "USER::";
   public static final long CACHE_USER_EXPIRE_TIME = 60 * 60 * 24L;

    private Constants() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }
}
