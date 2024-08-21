package site.leesoyeon.avalanche.auth.domain.email.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.leesoyeon.avalanche.auth.infrastructure.persistence.RedisService;
import site.leesoyeon.avalanche.auth.infrastructure.security.jwt.EmailJwtTokenProvider;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisService redisService;
    private final EmailService emailService;
    private final EmailJwtTokenProvider emailJwtTokenProvider;

    @Value("${email.verification.token.expiration-minutes}")
    private long tokenExpirationMinutes;

    public void sendVerificationEmail(String email) throws MessagingException {
        String token = emailJwtTokenProvider.createEmailVerificationToken(email);
        redisService.saveEmailVerificationToken(email, token, tokenExpirationMinutes, TimeUnit.MINUTES);

        String verificationLink = "http://localhost:9002/api/v1/auth/verify/" + token;
        emailService.sendVerificationEmail(email, "[Avalanche] 이메일 인증을 완료해 주세요", verificationLink);
    }

    public String verifyEmail(String token) {
        if (emailJwtTokenProvider.validateToken(token)) {
            String email = (String) emailJwtTokenProvider.getInfoFromToken(token).get("sub");
            String storedToken = redisService.getEmailVerificationToken(email);

            if (storedToken != null && storedToken.equals(token)) {
                redisService.deleteEmailVerificationToken(token);
                return email;
            }
        }
        return null;
    }
}