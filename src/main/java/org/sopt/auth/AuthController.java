package org.sopt.auth;

import org.sopt.auth.dto.LoginRequest;
import org.sopt.auth.dto.LoginResponse;
import org.sopt.auth.dto.SignupRequest;
import org.sopt.auth.message.AuthSuccessMessage;
import org.sopt.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@RequestBody SignupRequest request) {
        Long memberId = authService.signup(
                request.name(), request.email(), request.password()
        );
        return ResponseEntity.ok(ApiResponse.success(
                AuthSuccessMessage.SIGNED_UP.getMessage(),
                memberId
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        String accessToken = authService.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.success(
                AuthSuccessMessage.LOGGED_IN.getMessage(),
                new LoginResponse(accessToken)
        ));
    }
}
