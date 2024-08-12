package site.leesoyeon.probabilityrewardsystem.util;

import org.springframework.http.ResponseCookie;

import static site.leesoyeon.probabilityrewardsystem.common.Constants.REFRESH_TOKEN_EXPIRE_TIME;

public class CookieUtil {

    private CookieUtil() {
        throw new IllegalStateException("이 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static ResponseCookie createCookie(String cookieName, String cookieValue) {

        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
//                .secure(true) https를 사용하지 않는 경우 주석처리
                .path("/")
//                .domain(ALLOWED_ORIGINS)
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }
}
