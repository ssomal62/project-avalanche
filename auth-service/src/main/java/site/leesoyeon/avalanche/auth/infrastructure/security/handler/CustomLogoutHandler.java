package site.leesoyeon.avalanche.auth.infrastructure.security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler
//        implements LogoutHandler
{

//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        String token = jwtTokenProvider.resolveToken(request);
//
//        if (token != null) {
//            Map<String, Object> claims = jwtTokenProvider.getInfoFromToken(token);
//            String email = (String) claims.get("sub");
//
//            String clientId = request.getHeader("Client-Id");
//            if (clientId != null && !clientId.isEmpty()) {
//                // 개별 기기 로그아웃
//                jwtTokenProvider.deleteRefreshToken(email, clientId);
//                jwtTokenProvider.invalidateToken(token);
//
//            } else {
//                // 모든 기기 로그아웃
//                jwtTokenProvider.logoutAllClients(email, token);
//            }
//
//        }
//        SecurityContextHolder.clearContext();
//    }
}