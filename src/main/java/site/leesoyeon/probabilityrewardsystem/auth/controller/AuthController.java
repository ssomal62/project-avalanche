package site.leesoyeon.probabilityrewardsystem.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.probabilityrewardsystem.auth.dto.SignInRequestDto;
import site.leesoyeon.probabilityrewardsystem.auth.dto.SignInResponseDto;
import site.leesoyeon.probabilityrewardsystem.auth.service.AuthService;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtReissueResponseDto;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtRequestDto;
import site.leesoyeon.probabilityrewardsystem.user.dto.UpdatePasswordRequest;
import site.leesoyeon.probabilityrewardsystem.user.security.AuthenticatedUserInfo;

import java.util.concurrent.CompletableFuture;

/**
 * 인증 관련 API를 처리하는 컨트롤러 클래스입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(
            final @Valid @RequestBody SignInRequestDto signInRequestDto, HttpServletRequest httpRequest) {
        return authService.signIn(signInRequestDto, httpRequest);
    }

    /**
     * 주어진 이메일 주소로 인증 이메일을 발송합니다.
     *
     * @param email 인증 이메일을 발송할 사용자의 이메일 주소
     * @return 응답 엔티티
     */
    @PostMapping("/send-verification/{email}")
    public ResponseEntity<Void> sendVerificationEmail(@PathVariable(value = "email") String email) {
        CompletableFuture<Void> future = authService.sendVerificationEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 이메일 인증 토큰을 검증하고, 사용자의 계정을 활성화합니다.
     *
     * @param token 이메일 인증에 사용되는 JWT 토큰
     * @return 응답 엔티티
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> verifyEmail(@PathVariable(value = "token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/re-issue")
    public ResponseEntity<JwtReissueResponseDto> reissue(
            final @RequestBody JwtRequestDto jwtRequestDto
    ) {
        return authService.reissue(jwtRequestDto);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updateUser(
            final @AuthenticationPrincipal AuthenticatedUserInfo userInfo,
            final @Valid @RequestBody UpdatePasswordRequest requestDto
    ) {
        authService.updatePassword(userInfo, requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
