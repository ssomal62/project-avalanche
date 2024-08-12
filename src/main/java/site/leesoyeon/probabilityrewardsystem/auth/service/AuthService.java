package site.leesoyeon.probabilityrewardsystem.auth.service;

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
import site.leesoyeon.probabilityrewardsystem.auth.dto.SignInRequestDto;
import site.leesoyeon.probabilityrewardsystem.auth.dto.SignInResponseDto;
import site.leesoyeon.probabilityrewardsystem.auth.exception.AuthException;
import site.leesoyeon.probabilityrewardsystem.email.exception.EmailException;
import site.leesoyeon.probabilityrewardsystem.email.service.EmailVerificationService;
import site.leesoyeon.probabilityrewardsystem.jwt.JwtTokenProvider;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtReissueResponseDto;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtRequestDto;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtResponseDto;
import site.leesoyeon.probabilityrewardsystem.security.handler.CustomLogoutHandler;
import site.leesoyeon.probabilityrewardsystem.service.RedisService;
import site.leesoyeon.probabilityrewardsystem.user.dto.UpdatePasswordRequest;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserStatus;
import site.leesoyeon.probabilityrewardsystem.user.security.AuthenticatedUserInfo;
import site.leesoyeon.probabilityrewardsystem.user.service.UserService;

import java.util.concurrent.CompletableFuture;

import static site.leesoyeon.probabilityrewardsystem.util.CookieUtil.createCookie;
import static site.leesoyeon.probabilityrewardsystem.common.Constants.*;
import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.*;
import static site.leesoyeon.probabilityrewardsystem.user.enums.UserStatus.DELETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomLogoutHandler customLogoutHandler;
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

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

            User user = userService.findByEmail(email);

            if (user != null && user.getStatus() == UserStatus.PENDING) {
                user.updateStatus(UserStatus.ACTIVE)
                        .updateEmailVerified(true);
                userService.updateUser(user);
            } else {
                throw new EmailException(EMAIL_VERIFICATION_FAILURE);
            }
        } catch (Exception e) {
            throw new EmailException(EMAIL_VERIFICATION_FAILURE);
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,  // 읽기 커밋된 격리 수준
            propagation = Propagation.REQUIRED,    // 기본 전파 방식
            rollbackFor = Exception.class          // 모든 예외에 대해 롤백
    )
    public ResponseEntity<SignInResponseDto> signIn(@Valid SignInRequestDto requestDto, HttpServletRequest httpRequest) {
        String email = requestDto.email();
        String requestPassword = requestDto.password();
        User user = userService.findByEmail(email);

        checkUserStatusByEmail(user);
        validatePasswordCorrectness(requestPassword, user);

        String clientId = httpRequest.getHeader(CLIENT_ID_HEADER);
        if (clientId == null || clientId.isEmpty()) {
            throw new AuthException(CLIENT_ID_NULL_OR_EMPTY);
        }

        final JwtResponseDto jwtResponseDto = jwtTokenProvider.createAndSaveJwtToken(email, clientId);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, createCookie(AUTHORIZATION_KEY, jwtResponseDto.refreshToken()).toString())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtResponseDto.accessToken())
                .body(
                        SignInResponseDto.builder()
                                .userId(user.getUserId())
                                .nickname(user.getNickname())
                                .role(user.getRole())
                                .grantType(jwtResponseDto.grantType())
                                .accessToken(jwtResponseDto.accessToken())
                                .refreshToken(jwtResponseDto.refreshToken())
                                .expiresIn(jwtResponseDto.accessTokenExpiresIn())
                                .build()
                );
    }

    /**
     * 토큰 재발급
     *
     * @param jwtRequestDto 기존 토큰
     * @return 재발급된 토큰
     */
    @Transactional(readOnly = true)
    public ResponseEntity<JwtReissueResponseDto> reissue(final JwtRequestDto jwtRequestDto) {

        String clientId = jwtRequestDto.clientId();

        validateRefreshToken(jwtRequestDto);
        validateRefreshTokenOwnership(jwtRequestDto);

        String email = jwtTokenProvider.getInfoFromToken(jwtRequestDto.accessToken()).getSubject();

        JwtResponseDto jwtResponseDto = jwtTokenProvider.createAndSaveJwtToken(email, clientId);

        return ResponseEntity
                .ok()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtResponseDto.accessToken())
                .body(
                        JwtReissueResponseDto.builder()
                                .accessToken(jwtResponseDto.accessToken())
                                .refreshToken(jwtRequestDto.refreshToken())
                                .accessTokenExpiresIn(jwtResponseDto.accessTokenExpiresIn())
                                .build()
                );
    }

    @Transactional
    public void updatePassword(AuthenticatedUserInfo userInfo, @Valid UpdatePasswordRequest requestDto) {
        User user = userService.findByEmail(userInfo.getEmail());

        checkUserStatusByEmail(user);
        validateNewPasswordNotSameAsOld(requestDto.newPassword(), user);

        String encodedNewPassword = passwordEncoder.encode(requestDto.newPassword());
        user.updatePassword(encodedNewPassword);

        //TODO: 모든기기 로그아웃 처리
    }

//     ============================================
//                  Private Methods
//     ============================================

    protected void checkUserStatusByEmail(User user) {
        if (user.getStatus().equals(DELETED)) {
            throw new AuthException(DELETED_ACCOUNT);
        }
    }

    private void validatePasswordCorrectness(String requestPassword, User user) {
        if (!passwordEncoder.matches(requestPassword, user.getPassword())) {
            throw new AuthException(INVALID_ID_OR_PW); // 잘못된 비밀번호 예외
        }
    }

    private void validateNewPasswordNotSameAsOld(String requestPassword, User user){
        if (passwordEncoder.matches(requestPassword, user.getPassword())) {
            throw new AuthException(SAME_AS_OLD_PASSWORD); // 새 비밀번호가 기존 비밀번호와 동일할 때 예외 처리
        }
    }

    private void validateRefreshToken(JwtRequestDto jwtRequestDto) {
        jwtTokenProvider.validateToken(jwtRequestDto.refreshToken());
    }

    private void validateRefreshTokenOwnership(JwtRequestDto jwtRequestDto) {
        String email = jwtTokenProvider.getInfoFromToken(jwtRequestDto.accessToken()).getSubject();
        String validRefreshToken = redisService.getData(email);
        if (!jwtRequestDto.refreshToken().equals(validRefreshToken)) {
            throw new AuthException(EXPIRED_REFRESH_TOKEN);
        }
    }

}