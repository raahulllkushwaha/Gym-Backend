package com.gym.auth;

import com.gym.auth.dto.LoginRequest;
import com.gym.auth.dto.LoginResponse;
import com.gym.auth.dto.RefreshTokenRequest;
import com.gym.auth.dto.SignupRequestDto;
import com.gym.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
    }

    /** Public: anyone can submit -> goes to PENDING_APPROVAL, no login access until Manager approves. */
    @PostMapping("/signup-request")
    public ResponseEntity<ApiResponse<Void>> signupRequest(@Valid @RequestBody SignupRequestDto dto) {
        authService.submitSignupRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Signup request submitted. You will be notified once approved by the gym manager."));
    }
}
