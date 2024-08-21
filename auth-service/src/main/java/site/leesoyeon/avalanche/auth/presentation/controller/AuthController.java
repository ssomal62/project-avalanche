package site.leesoyeon.avalanche.auth.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.auth.application.dto.SignInRequestDto;
import site.leesoyeon.avalanche.auth.application.service.AuthService;
import site.leesoyeon.avalanche.auth.infrastructure.security.jwt.TokenPair;

import java.util.UUID;

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
    public ResponseEntity<TokenPair> signIn(
            final @Valid @RequestBody SignInRequestDto signInRequestDto, HttpServletRequest httpRequest) {
        return authService.signIn(signInRequestDto, httpRequest);
    }

    /**
     * 주어진 이메일 주소로 인증 이메일을 발송합니다.
     */
    @PostMapping("/send-verification/{email}")
    public ResponseEntity<Void> sendVerificationEmail(@PathVariable("email") String email) {
        authService.sendVerificationEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 이메일 인증 토큰을 검증하고, 사용자의 계정을 활성화합니다.
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> verifyEmail(@PathVariable("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/re-issue")
    public ResponseEntity<TokenPair> reissue(
            final @RequestHeader("User-Id")UUID userId,
            final @RequestBody TokenPair jwtDto,
            HttpServletRequest httpRequest
    ) {
        return authService.reissue(userId, jwtDto, httpRequest);
    }

    /**
     * 비밀번호 발급
     */
    @PostMapping( "/encode-password" )
    public ResponseEntity<String> encodePassword(@RequestBody String rawPassword) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.encodePassword(rawPassword));
    }

//    @PutMapping("/update-password")
//    public ResponseEntity<Void> updateUser(
//            final @RequestHeader("User-Id") UUID userId,
//            final @Valid @RequestBody UpdatePasswordRequest requestDto
//    ) {
//        authService.updatePassword(userId, requestDto);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
