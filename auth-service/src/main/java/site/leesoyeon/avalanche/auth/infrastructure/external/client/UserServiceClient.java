package site.leesoyeon.avalanche.auth.infrastructure.external.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.auth.infrastructure.external.dto.UpdateAuthenticatedUserDto;
import site.leesoyeon.avalanche.auth.infrastructure.external.dto.UpdatePasswordDto;
import site.leesoyeon.avalanche.auth.infrastructure.external.dto.UserDto;

@FeignClient(name = "user-service", url = "${feign.client.config.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/v1/user/{email}")
    UserDto findByEmail(@PathVariable("email") String email);

    @PostMapping("/api/v1/user/email-verified")
    void updateEmailVerified(@RequestBody UpdateAuthenticatedUserDto request);

    @PutMapping("/api/v1/user/update-password")
    void updatePassword(@RequestBody UpdatePasswordDto request);

}