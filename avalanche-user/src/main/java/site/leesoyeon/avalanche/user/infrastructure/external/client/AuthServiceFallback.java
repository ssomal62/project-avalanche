package site.leesoyeon.avalanche.user.infrastructure.external.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import site.leesoyeon.avalanche.user.infrastructure.external.dto.TokenValidationRequest;
import site.leesoyeon.avalanche.user.infrastructure.external.dto.TokenValidationResponse;

@Component
public class AuthServiceFallback implements AuthServiceClient {
    @Override
    public ResponseEntity<TokenValidationResponse> validateToken(TokenValidationRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public String encodePassword(String rawPassword) {
        return "";
    }
}