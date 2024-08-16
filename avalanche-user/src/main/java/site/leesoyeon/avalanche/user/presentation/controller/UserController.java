package site.leesoyeon.avalanche.user.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.user.presentation.dto.UpdateUserInfoRequestDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserDetailDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserRegistrationDto;
import site.leesoyeon.avalanche.user.application.service.UserRegistrationService;
import site.leesoyeon.avalanche.user.application.service.UserService;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<UserDetailDto> getUser(
            @RequestHeader("User-Id") UUID userId
    ) {
        UserDetailDto userDetail = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userDetail);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> registerUser(
            final @Valid @RequestBody UserRegistrationDto requestDto) {
        registrationService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(
            @RequestHeader("User-Id") UUID userId,
            @Valid @RequestBody UpdateUserInfoRequestDto requestDto
    ) {
        userService.updateUser(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateUser(
            @RequestHeader("User-Id") UUID userId
    ) {
        userService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
