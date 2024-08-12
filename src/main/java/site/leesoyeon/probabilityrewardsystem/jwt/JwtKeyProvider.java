package site.leesoyeon.probabilityrewardsystem.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.leesoyeon.probabilityrewardsystem.jwt.config.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@Slf4j
public class JwtKeyProvider {

    private final SecretKey key;
    private final SignatureAlgorithm signatureAlgorithm;


    public JwtKeyProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.signatureAlgorithm = SignatureAlgorithm.forName(jwtProperties.getSignatureAlgorithm());
    }

    public SecretKey getKey() {
        return key;
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}