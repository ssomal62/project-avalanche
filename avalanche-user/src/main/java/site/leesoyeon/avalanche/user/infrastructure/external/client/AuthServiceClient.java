package site.leesoyeon.avalanche.user.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.leesoyeon.avalanche.user.infrastructure.external.dto.TokenValidationRequest;
import site.leesoyeon.avalanche.user.infrastructure.external.dto.TokenValidationResponse;

@FeignClient(name = "auth-service", fallback = AuthServiceFallback.class)
public interface AuthServiceClient {
    @PostMapping("/api/v1/auth/validate-token")
    ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request);

    @PostMapping("/auth/encode-password")
    String encodePassword(@RequestBody String rawPassword);
}

