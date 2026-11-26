package org.sopt.auth;

import org.sopt.auth.dto.LoginRequest;
import org.sopt.auth.dto.SignupRequest;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.message.AuthSuccessMessage;
import org.sopt.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.Authentication;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final long refreshTokenExpiration;
    private final boolean cookieSecure;

    public AuthController(AuthService authService,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${jwt.cookie-secure}") boolean cookieSecure) {
        this.authService = authService;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.cookieSecure = cookieSecure;
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
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody LoginRequest request) {
        return tokenResponse(authService.login(request.email(), request.password()),
                AuthSuccessMessage.LOGGED_IN.getMessage());
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return tokenResponse(authService.reissue(refreshToken), AuthSuccessMessage.TOKEN_REISSUED.getMessage());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        authService.logout((Long) authentication.getPrincipal(), (String) authentication.getDetails());
        return clearCookieResponse(AuthSuccessMessage.LOGGED_OUT.getMessage());
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(Authentication authentication) {
        authService.withdraw((Long) authentication.getPrincipal());
        return clearCookieResponse(AuthSuccessMessage.WITHDRAWN.getMessage());
    }

    private ResponseEntity<ApiResponse<Void>> clearCookieResponse(String message) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString())
                .body(ApiResponse.success(message, null));
    }

    private ResponseEntity<ApiResponse<Void>> tokenResponse(TokenPair tokens, String message) {
        ResponseCookie cookie = refreshCookie(tokens.refreshToken(), Duration.ofMillis(refreshTokenExpiration));
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.accessToken())
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(message, null));
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(maxAge)
                .build();
    }
}
