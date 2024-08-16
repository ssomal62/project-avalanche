package site.leesoyeon.avalanche.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.domain.repository.UserRepository;
import site.leesoyeon.avalanche.user.infrastructure.exception.UserNotFoundException;
import site.leesoyeon.avalanche.user.presentation.dto.UpdateUserInfoRequestDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserDetailDto;
import site.leesoyeon.avalanche.user.shared.enums.UserStatus;
import site.leesoyeon.avalanche.user.util.UserMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDetailDto getUser(UUID userId) {
        return userMapper.toDto(findById(userId));
    }

    @Transactional
    public void updateUser(UUID userId, UpdateUserInfoRequestDto requestDto) {
        User user = findById(userId);

        user.updateNickname(requestDto.nickname())
            .updatePhone(requestDto.phone())
            .updateAddress(requestDto.address(), requestDto.detailedAddress());
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = findById(userId);
        user.updateStatus(UserStatus.DELETED);
        //TODO : 리프레쉬 토큰 삭제, 액세스토큰 블랙리스트 처리
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}