package site.leesoyeon.probabilityrewardsystem.security.handler;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.jwt.JwtTokenProvider;
import site.leesoyeon.probabilityrewardsystem.jwt.RefreshTokenService;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            Claims claims = jwtTokenProvider.getInfoFromToken(token);
            String email = claims.getSubject();

            String clientId = request.getHeader("Client-Id");
            if (clientId != null && !clientId.isEmpty()) {
                // 개별 기기 로그아웃
                refreshTokenService.deleteRefreshToken(email, clientId);
                jwtTokenProvider.invalidateToken(token);

            } else {
                // 모든 기기 로그아웃
                jwtTokenProvider.logoutAllClients(email, token);
            }

        }
        SecurityContextHolder.clearContext();
    }
}