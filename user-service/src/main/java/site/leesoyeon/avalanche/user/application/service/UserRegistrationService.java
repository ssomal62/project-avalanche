package site.leesoyeon.avalanche.user.application.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.domain.repository.UserRepository;
import site.leesoyeon.avalanche.user.infrastructure.external.client.AuthServiceClient;
import site.leesoyeon.avalanche.user.presentation.dto.UpdateAuthenticatedUserDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserRegistrationDto;
import site.leesoyeon.avalanche.user.util.UserMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final AuthServiceClient authServiceClient;
    private final UserService userService;


    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        User user = createUser(registrationDto);

        CompletableFuture<Void> emailFuture = CompletableFuture.runAsync(() -> {
            authServiceClient.sendVerificationEmail(user.getEmail());
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

        String encodedPassword = authServiceClient.encodePassword(registrationDto.password());
        user.setPassword(encodedPassword);

        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public void updateEmailVerified(@Valid UpdateAuthenticatedUserDto requestDto) {
        userService.updateUser(requestDto);
    }

}
