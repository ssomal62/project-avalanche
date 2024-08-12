package site.leesoyeon.probabilityrewardsystem.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.auth.service.AuthService;
import site.leesoyeon.probabilityrewardsystem.user.dto.UserRegistrationDto;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.repository.UserRepository;
import site.leesoyeon.probabilityrewardsystem.user.util.UserMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        User user = createUser(registrationDto);

        CompletableFuture<Void> emailFuture = authService.sendVerificationEmail(user.getEmail());

        CompletableFuture.allOf(emailFuture)
                .thenRun(() -> {
                    log.info("사용자 등록, 이메일 전송, 알림 전송 모두 완료: {}", user.getEmail());
                })
                .exceptionally(ex -> {
                    log.error("사용자 등록 후 처리 중 오류 발생: {}", user.getEmail(), ex);
                    return null;
                });

    }

    @Transactional
    public User createUser(UserRegistrationDto registrationDto) {
        User user = userMapper.toUser(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.password()));
        userRepository.save(user);
        return user;
    }
}
