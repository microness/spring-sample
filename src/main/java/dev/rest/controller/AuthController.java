package dev.rest.controller;

import dev.rest.dto.LoginRequest;
import dev.rest.dto.LoginResponse;
import dev.rest.dto.SignupRequest;
import dev.rest.dto.SignupResponse;
import dev.rest.model.User;
import dev.rest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        System.out.println("request = " + request);
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/userInfo")
    public void getMyInfo(@AuthenticationPrincipal User user) {
        System.out.println("user = " + user);
    }

}
