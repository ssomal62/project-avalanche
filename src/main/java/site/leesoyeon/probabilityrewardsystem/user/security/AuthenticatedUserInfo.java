package site.leesoyeon.probabilityrewardsystem.user.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import site.leesoyeon.probabilityrewardsystem.auth.exception.AuthException;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_USER;

/**
 * AuthenticatedUserInfo 클래스는 Spring Security의 {@link UserDetails} 인터페이스를 구현하여
 * 인증된 사용자의 정보를 제공하는 역할을 합니다.
 * 이 클래스는 사용자의 권한 및 인증 정보를 반환하며,
 * 주어진 {@link User} 객체를 기반으로 동작합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>사용자의 권한 목록을 반환 ({@link #getAuthorities()})</li>
 *   <li>사용자의 이메일 주소를 사용자명으로 반환 ({@link #getUsername()})</li>
 *   <li>사용자 정보가 유효한지 확인하기 위해 생성자에서 {@link User} 객체가 null인지 검사</li>
 * </ul>
 *
 * <p>이 클래스는 Spring Security에서 인증된 사용자의 정보를 제공하기 위해 사용됩니다.
 * {@link UserDetailsService}와 연계하여, 인증된 사용자의 세부 정보를
 * Spring Security 컨텍스트에 전달하는 역할을 합니다.
 *
 * @see UserDetails
 * @see UserDetailsService
 * @see User
 */

@Getter
public class AuthenticatedUserInfo implements UserDetails {

    private final User user;
    private final String email;
    private final UUID userId;

    public AuthenticatedUserInfo(User user) {
        if (user == null) {
            throw new AuthException(NOT_FOUND_USER);
        }
        this.user = user;
        this.email = user.getEmail();
        this.userId = user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole userRole = user.getRole();
        String authority = userRole.getAuthority();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

}