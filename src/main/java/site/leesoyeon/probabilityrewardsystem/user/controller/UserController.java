package site.leesoyeon.probabilityrewardsystem.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.probabilityrewardsystem.user.dto.UpdateUserInfoRequest;
import site.leesoyeon.probabilityrewardsystem.user.dto.UserRegistrationDto;
import site.leesoyeon.probabilityrewardsystem.user.security.AuthenticatedUserInfo;
import site.leesoyeon.probabilityrewardsystem.user.service.UserRegistrationService;
import site.leesoyeon.probabilityrewardsystem.user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> registerUser(final @Valid @RequestBody UserRegistrationDto requestDto) {
        registrationService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(
            final @AuthenticationPrincipal AuthenticatedUserInfo userInfo,
            final @Valid @RequestBody UpdateUserInfoRequest requestDto
    ) {
        userService.updateUser(userInfo, requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateUser(
            final @AuthenticationPrincipal AuthenticatedUserInfo userInfo
    ) {
        userService.deactivateUser(userInfo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
