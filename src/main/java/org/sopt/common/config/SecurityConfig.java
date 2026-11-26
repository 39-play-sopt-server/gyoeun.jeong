package org.sopt.common.config;

import org.sopt.auth.ApiAuthenticationEntryPoint;
import org.sopt.auth.ApiAccessDeniedHandler;
import org.sopt.auth.jwt.JwtAuthenticationFilter;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.member.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final ApiAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtProvider jwtProvider, ApiAuthenticationEntryPoint authenticationEntryPoint,
                          RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository,
                          ApiAccessDeniedHandler accessDeniedHandler) {
        this.jwtProvider = jwtProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/reissue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/members").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, refreshTokenRepository, memberRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
