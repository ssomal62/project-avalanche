package site.leesoyeon.avalanche.user.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.user.application.service.UserRegistrationService;
import site.leesoyeon.avalanche.user.application.service.UserService;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.domain.model.UserDto;
import site.leesoyeon.avalanche.user.presentation.dto.*;

import java.util.List;
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

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userDetailList = userService.getAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(userDetailList);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> registerUser(
            final @Valid @RequestBody UserRegistrationDto requestDto) {
        registrationService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/email-verified")
    public ResponseEntity<Void> updateEmailVerified(
            final @Valid @RequestBody UpdateAuthenticatedUserDto requestDto) {
        registrationService.updateEmailVerified(requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> findByEmail(@PathVariable("email") String email){
        UserDto userDetail = userService.getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(userDetail);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @RequestHeader("User-Id") UUID userId,
            @RequestBody UpdatePasswordDto request) {
        userService.updatePassword(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
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
