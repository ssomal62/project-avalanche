package site.leesoyeon.avalanche.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.domain.repository.UserRepository;
import site.leesoyeon.avalanche.user.infrastructure.external.client.AuthServiceClient;
import site.leesoyeon.avalanche.user.presentation.dto.UserRegistrationDto;
import site.leesoyeon.avalanche.user.util.UserMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final AuthServiceClient authServiceClient;  //TODO: AuthServiceClient 주입


    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        User user = createUser(registrationDto);

        CompletableFuture<Void> emailFuture = CompletableFuture.runAsync(() -> {
            // TODO: 이메일 인증 및 알림 서비스 호출 (Auth 서비스의 다른 API와 연동 가능)
            // authService.sendVerificationEmail(user.getEmail());
        });

        CompletableFuture.allOf(emailFuture)
                .thenRun(() -> log.info("사용자 등록, 이메일 전송, 알림 전송 모두 완료: {}", user.getEmail()))
                .exceptionally(ex -> {
                    log.error("사용자 등록 후 처리 중 오류 발생: {}", user.getEmail(), ex);
                    return null;
                });

    }

    @Transactional
    public User createUser(UserRegistrationDto registrationDto) {
        User user = userMapper.toUser(registrationDto);

        // TODO: AuthServiceClient를 사용해 비밀번호 인코딩
        String encodedPassword = authServiceClient.encodePassword(registrationDto.password());
        user.setPassword(encodedPassword);

        user = userRepository.save(user);
        return user;
    }
}
