package site.leesoyeon.avalanche.auth.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.auth.application.dto.SignInRequestDto;
import site.leesoyeon.avalanche.auth.domain.email.service.EmailVerificationService;
import site.leesoyeon.avalanche.auth.infrastructure.exception.AuthException;
import site.leesoyeon.avalanche.auth.infrastructure.exception.EmailException;
import site.leesoyeon.avalanche.auth.infrastructure.external.client.UserServiceClient;
import site.leesoyeon.avalanche.auth.infrastructure.external.dto.UpdateAuthenticatedUserDto;
import site.leesoyeon.avalanche.auth.infrastructure.external.dto.UserDto;
import site.leesoyeon.avalanche.auth.infrastructure.persistence.RedisService;
import site.leesoyeon.avalanche.auth.infrastructure.security.jwt.JwtTokenProvider;
import site.leesoyeon.avalanche.auth.infrastructure.security.jwt.TokenPair;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static site.leesoyeon.avalanche.auth.application.util.Constants.BEARER_PREFIX;
import static site.leesoyeon.avalanche.auth.application.util.Constants.CLIENT_ID_HEADER;
import static site.leesoyeon.avalanche.auth.shared.api.ApiStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Async
    public CompletableFuture<Void> sendVerificationEmail(String email) {
        return CompletableFuture.runAsync(() -> {
            try {
                emailVerificationService.sendVerificationEmail(email);
            } catch (Exception e) {
                throw new EmailException(EMAIL_SEND_FAILURE);
            }
        });
    }

    @Transactional
    public void verifyEmail(String token) {
        try {
            String email = emailVerificationService.verifyEmail(token);
            if (email == null) {
                throw new EmailException(INVALID_TOKEN);
            }

            UserDto user = userServiceClient.findByEmail(email);
            if (user != null && user.status().equals("PENDING")) {
                userServiceClient.updateEmailVerified(
                        UpdateAuthenticatedUserDto.builder()
                                .email(email)
                                .emailVerified(true)
                                .status("ACTIVE")
                                .build());
            } else {
                throw new EmailException(EMAIL_VERIFICATION_FAILURE);
            }
        } catch (Exception e) {
            throw new EmailException(EMAIL_VERIFICATION_FAILURE);
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public ResponseEntity<TokenPair> signIn(@Valid SignInRequestDto requestDto, HttpServletRequest httpRequest) {
        String email = requestDto.email();
        String requestPassword = requestDto.password();
        UserDto user = userServiceClient.findByEmail(email);

        String clientId = httpRequest.getHeader(CLIENT_ID_HEADER);
        if (clientId == null || clientId.isEmpty()) {
            throw new AuthException(CLIENT_ID_NULL_OR_EMPTY);
        }

        checkUserStatusByEmail(user);
        validatePasswordCorrectness(requestPassword, user);

        TokenPair jwtResponseDto = tokenProvider.createTokenPair(user.userId(), clientId, user.role());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtResponseDto.accessToken())
                .header("Refresh-Token", jwtResponseDto.refreshToken())
                .body(jwtResponseDto);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public ResponseEntity<TokenPair> reissue(UUID userId, TokenPair jwtDto, HttpServletRequest httpRequest) {

        if (redisService.isBlacklisted(jwtDto.refreshToken())) {
            throw new RuntimeException("Invalid refresh token: This token has been blacklisted");
        }
        String clientId = httpRequest.getHeader(CLIENT_ID_HEADER);
        String accessToken = jwtTokenProvider.refreshAccessToken(userId, clientId, jwtDto);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
                .header("Refresh-Token", jwtDto.refreshToken())
                .body(
                        TokenPair.builder()
                                .accessToken(accessToken)
                                .refreshToken(jwtDto.refreshToken())
                                .build()
                );
    }

//    @Transactional
//    public void updatePassword(AuthenticatedUserInfo userInfo, @Valid UpdatePasswordRequest requestDto, HttpServletRequest request) {
//        UserDto user = userServiceClient.findByEmail(userInfo.getEmail());
//        String token = jwtTokenProvider.resolveToken(request);
//
//        checkUserStatusByEmail(user);
//        validateNewPasswordNotSameAsOld(requestDto.newPassword(), user);
//
//        String encodedNewPassword = passwordEncoder.encode(requestDto.newPassword());
//        userServiceClient.updatePassword(
//                UpdatePasswordDto
//                        .builder()
//                        .newPassword(encodedNewPassword)
//                        .build());
//
//        //TODO: 모든기기 로그아웃 처리
//        jwtTokenProvider.logoutAllClients(user.email(), token);
//    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

//     ============================================
//                  Private Methods
//     ============================================

    protected void checkUserStatusByEmail(UserDto user) {
        if (user.status().equals("DELETE")) {
            throw new AuthException(DELETED_ACCOUNT);
        }
    }

    private void validatePasswordCorrectness(String requestPassword, UserDto user) {
        if (!passwordEncoder.matches(requestPassword, user.password())) {
            throw new AuthException(INVALID_ID_OR_PW); // 잘못된 비밀번호 예외
        }
    }

    private void validateNewPasswordNotSameAsOld(String requestPassword, UserDto user) {
        if (passwordEncoder.matches(requestPassword, user.password())) {
            throw new AuthException(SAME_AS_OLD_PASSWORD); // 새 비밀번호가 기존 비밀번호와 동일할 때 예외 처리
        }
    }
}