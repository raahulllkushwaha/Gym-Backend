package com.gym.auth;

import com.gym.auth.dto.LoginRequest;
import com.gym.auth.dto.LoginResponse;
import com.gym.auth.dto.SignupRequestDto;
import com.gym.security.CustomUserDetails;
import com.gym.security.CustomUserDetailsService;
import com.gym.security.JwtUtil;
import com.gym.user.User;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final SignupRequestRepository signupRequestRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndDeletedFalse(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.LocalDateTime.now())) {
            throw new org.springframework.security.authentication.LockedException(
                    "Account locked due to multiple failed attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException ex) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(java.time.LocalDateTime.now().plusMinutes(30));
            }
            userRepository.save(user);
            throw ex;
        }

        // reset counters on success
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        CustomUserDetails principal = (CustomUserDetails) userDetails;

        return LoginResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(principal))
                .refreshToken(jwtUtil.generateRefreshToken(principal))
                .username(user.getUsername())
                .role(user.getRole().name())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    public LoginResponse refresh(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        String type = jwtUtil.extractTokenType(refreshToken);

        if (!"REFRESH".equals(type) || !jwtUtil.isTokenValid(refreshToken, username)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        CustomUserDetails principal = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        return LoginResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(principal))
                .refreshToken(jwtUtil.generateRefreshToken(principal))
                .username(principal.getUsername())
                .role(principal.getRole())
                .mustChangePassword(false)
                .build();
    }

    @Transactional
    public void submitSignupRequest(SignupRequestDto dto) {
        if (signupRequestRepository.existsByEmailAndStatus(dto.email(), SignupRequestStatus.PENDING)) {
            throw new IllegalStateException("A signup request with this email is already pending approval");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("This email is already registered");
        }

        SignupRequest signupRequest = SignupRequest.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .age(dto.age())
                .gender(dto.gender())
                .preferredPlanName(dto.preferredPlanName())
                .message(dto.message())
                .status(SignupRequestStatus.PENDING)
                .build();

        signupRequestRepository.save(signupRequest);
        // Manager sees this in dashboard "Pending Requests" - no credentials issued yet.
    }
}
