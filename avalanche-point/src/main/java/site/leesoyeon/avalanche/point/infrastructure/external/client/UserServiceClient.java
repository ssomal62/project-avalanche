package site.leesoyeon.avalanche.point.infrastructure.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.point.infrastructure.external.dto.UserDto;


import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{email}")
    UserDto findByEmail(@PathVariable(value = "email") String email);

    @PutMapping("/api/v1/users/password")
    void updatePassword(@RequestHeader("User-Id") UUID userId, @RequestBody String newPassword);

    @PutMapping("/api/v1/users/{userId}/status")
    void updateUserStatus(@PathVariable("userId") UUID userId, @RequestBody String status);

    @PutMapping("/api/v1/users/{userId}/email-verified")
    void updateEmailVerified(@PathVariable("userId") UUID userId, @RequestBody boolean verified);
}