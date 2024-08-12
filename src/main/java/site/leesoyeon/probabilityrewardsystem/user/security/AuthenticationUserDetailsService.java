package site.leesoyeon.probabilityrewardsystem.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.exception.UserException;
import site.leesoyeon.probabilityrewardsystem.user.repository.UserRepository;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_USER;

/**
 * AuthenticationUserDetailsService 클래스는 Spring Security의 {@link UserDetailsService}를 구현하여
 * 사용자의 인증 정보를 로드하는 역할을 합니다.
 *
 * <p>이 클래스는 주어진 사용자 이름(이 경우 이메일)을 기반으로 데이터베이스에서 사용자를 조회하고,
 * 조회된 사용자 정보를 기반으로 {@link AuthenticatedUserInfo} 객체를 반환합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>이메일을 기반으로 사용자를 조회 ({@link #loadUserByUsername(String)})</li>
 *   <li>사용자를 찾을 수 없을 경우 {@link UserException}을 던짐</li>
 *   <li>조회된 사용자를 {@link AuthenticatedUserInfo} 객체로 래핑하여 반환</li>
 * </ul>
 *
 * <p>이 클래스는 Spring Security와 통합되어 사용자 인증 로직의 핵심 부분을 담당합니다.
 * 사용자 인증 요청이 들어올 때, 이 서비스를 통해 사용자 정보를 로드하여 인증 절차를 진행합니다.
 *
 * @see UserDetailsService
 * @see AuthenticatedUserInfo
 * @see User
 */
@Service
@RequiredArgsConstructor
public class AuthenticationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));
        return new AuthenticatedUserInfo(user);
    }
}
