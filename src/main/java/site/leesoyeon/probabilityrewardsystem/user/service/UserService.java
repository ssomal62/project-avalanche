package site.leesoyeon.probabilityrewardsystem.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;
import site.leesoyeon.probabilityrewardsystem.user.dto.UpdateUserInfoRequest;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserStatus;
import site.leesoyeon.probabilityrewardsystem.user.exception.UserException;
import site.leesoyeon.probabilityrewardsystem.user.repository.UserRepository;
import site.leesoyeon.probabilityrewardsystem.user.security.AuthenticatedUserInfo;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUser(AuthenticatedUserInfo userInfo, UpdateUserInfoRequest requestDto) {
        User user = findByEmail(userInfo.getEmail());

        user.updateNickname(requestDto.nickname())
            .updatePhone(requestDto.phone())
            .updateAddress(requestDto.address(), requestDto.detailedAddress());
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(AuthenticatedUserInfo userInfo) {
        User user = findByEmail(userInfo.getEmail());
        user.updateStatus(UserStatus.DELETED);

        //TODO : 리프레쉬 토큰 삭제, 액세스토큰 블랙리스트 처리
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(ApiStatus.NOT_FOUND_USER));
    }

}