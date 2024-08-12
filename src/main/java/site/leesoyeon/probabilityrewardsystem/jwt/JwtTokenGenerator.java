package site.leesoyeon.probabilityrewardsystem.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.jwt.config.JwtProperties;
import site.leesoyeon.probabilityrewardsystem.jwt.dto.JwtResponseDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.stream.Collectors;

import static site.leesoyeon.probabilityrewardsystem.common.Constants.BEARER_PREFIX;

@Component
public class JwtTokenGenerator {
    private final JwtKeyProvider jwtKeyProvider;
    private final JwtProperties jwtProperties;

    public JwtTokenGenerator(JwtKeyProvider jwtKeyProvider, JwtProperties jwtProperties) {
        this.jwtKeyProvider = jwtKeyProvider;
        this.jwtProperties = jwtProperties;
    }

    public JwtResponseDto createJwtToken(Authentication authentication) {
        String authorities = extractAuthorities(authentication);
        long now = System.currentTimeMillis();

        String accessToken = createToken(authentication.getName(), authorities, now, jwtProperties.getAccessTokenValidity());
        String refreshToken = createToken(null, null, now, jwtProperties.getRefreshTokenValidity());

        return JwtResponseDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtProperties.getAccessTokenValidity())
                .build();
    }


    public String createEmailVerificationToken(String email) {
        long now = System.currentTimeMillis();
        Date tokenExpiration = new Date(now + jwtProperties.getEmailVerificationTokenValidity());

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(tokenExpiration)
                .signWith(jwtKeyProvider.getKey(), jwtKeyProvider.getSignatureAlgorithm())
                .compact();
    }

    private String createToken(String subject, String authorities, long now, long validity) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("auth", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validity))
                .signWith(jwtKeyProvider.getKey(), jwtKeyProvider.getSignatureAlgorithm())
                .compact();
    }

    private String extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
