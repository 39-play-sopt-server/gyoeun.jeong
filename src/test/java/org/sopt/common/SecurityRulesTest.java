package org.sopt.common;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sopt.auth.ApiAccessDeniedHandler;
import org.sopt.auth.ApiAuthenticationEntryPoint;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.common.config.CorsConfig;
import org.sopt.common.config.SecurityConfig;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRole;
import org.sopt.member.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityRulesTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mvc;
    private JwtProvider jwt;
    private MemberRepository members;
    private RefreshTokenRepository sessions;

    @BeforeEach
    void setup() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfig.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters((Filter) context.getBean("springSecurityFilterChain"))
                .build();
        jwt = context.getBean(JwtProvider.class);
        members = context.getBean(MemberRepository.class);
        sessions = context.getBean(RefreshTokenRepository.class);
    }

    @AfterEach
    void close() {
        context.close();
    }

    @Test
    void publicPathsAndProtectedPostAreEnforced() throws Exception {
        mvc.perform(get("/posts")).andExpect(status().isOk());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mvc.perform(post("/posts")).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/logout")).andExpect(status().isUnauthorized());
    }

    @Test
    void memberListRequiresAdminButMeAllowsUsers() throws Exception {
        Member member = new Member("이름", "user@example.com", "pw");
        when(sessions.existsBySessionIdAndMemberId("session-1", 1L)).thenReturn(true);
        when(members.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(member));
        String bearer = "Bearer " + jwt.createAccessToken(1L, "session-1");

        mvc.perform(get("/members")).andExpect(status().isUnauthorized());
        mvc.perform(get("/members/me").header("Authorization", bearer)).andExpect(status().isOk());
        mvc.perform(get("/members").header("Authorization", bearer))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.success").value(false));
        ReflectionTestUtils.setField(member, "role", MemberRole.ROLE_ADMIN);
        mvc.perform(get("/members").header("Authorization", bearer)).andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, CorsConfig.class, TestController.class})
    static class TestConfig {
        @Bean JwtProvider jwtProvider() {
            return new JwtProvider(Base64.getEncoder().encodeToString(new byte[32]), 1800000, 604800000);
        }
        @Bean MemberRepository memberRepository() { return mock(MemberRepository.class); }
        @Bean RefreshTokenRepository refreshTokenRepository() { return mock(RefreshTokenRepository.class); }
        @Bean ApiAuthenticationEntryPoint entryPoint() { return new ApiAuthenticationEntryPoint(new ObjectMapper()); }
        @Bean ApiAccessDeniedHandler accessDeniedHandler() { return new ApiAccessDeniedHandler(new ObjectMapper()); }
    }

    @RestController
    static class TestController {
        @GetMapping({"/posts", "/members", "/members/me", "/swagger-ui/index.html", "/v3/api-docs"})
        String get() { return "ok"; }
        @PostMapping({"/posts", "/auth/logout"})
        String post() { return "ok"; }
    }
}
