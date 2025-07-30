package site.leesoyeon.avalanche.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.domain.model.UserDto;
import site.leesoyeon.avalanche.user.domain.repository.UserRepository;
import site.leesoyeon.avalanche.user.infrastructure.exception.UserNotFoundException;
import site.leesoyeon.avalanche.user.presentation.dto.UpdateAuthenticatedUserDto;
import site.leesoyeon.avalanche.user.presentation.dto.UpdatePasswordDto;
import site.leesoyeon.avalanche.user.presentation.dto.UpdateUserInfoRequestDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserDetailDto;
import site.leesoyeon.avalanche.user.shared.enums.UserStatus;
import site.leesoyeon.avalanche.user.util.UserMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDetailDto getUser(UUID userId) {
        return userMapper.toDto(findById(userId));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userMapper.toDtoForAuth(findByEmail(email));
    }

    @Transactional
    public void updateUser(UUID userId, UpdateUserInfoRequestDto requestDto) {
        User user = findById(userId);

        user.updateNickname(requestDto.nickname())
            .updatePhone(requestDto.phone())
            .updateAddress(requestDto.address(), requestDto.detailedAddress());
    }

    @Transactional
    public void updateUser(UpdateAuthenticatedUserDto requestDto) {
        User user = findByEmail(requestDto.email());
        log.info("데이터확인 {}: {}", requestDto.email(), requestDto);
        user.updateEmailVerified(requestDto.emailVerified())
            .updateStatus(requestDto.status());
    }

    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordDto request) {
        User user = findById(userId);
        user.updatePassword(request.newPassword());
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = findById(userId);
        user.updateStatus(UserStatus.DELETED);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}