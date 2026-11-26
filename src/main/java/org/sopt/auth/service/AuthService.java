package org.sopt.auth.service;

import org.sopt.auth.exception.DuplicateEmailException;
import org.sopt.auth.exception.InvalidCredentialsException;
import org.sopt.auth.exception.InvalidSignupException;
import org.sopt.auth.message.AuthErrorMessage;
import org.sopt.auth.dto.TokenPair;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public Long signup(String name, String email, String password) {
        validateSignup(name, email, password);
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        Member member = new Member(name.strip(), email, passwordEncoder.encode(password));
        return memberRepository.save(member).getId();
    }

    @Transactional
    public TokenPair login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidCredentialsException();
        }
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        member = memberRepository.findActiveForUpdate(member.getId())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenService.issueForLogin(member.getId());
    }

    @Transactional
    public TokenPair reissue(String token) {
        return tokenService.reissue(token);
    }

    @Transactional
    public void logout(Long memberId, String sessionId) {
        tokenService.logout(memberId, sessionId);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findActiveForUpdate(memberId)
                .orElseThrow(InvalidCredentialsException::new);
        tokenService.revokeAll(memberId);
        member.withdraw();
    }

    private void validateSignup(String name, String email, String password) {
        if (name == null || name.isBlank()) {
            throw new InvalidSignupException(AuthErrorMessage.NAME_REQUIRED);
        }
        if (name.strip().length() > 50) {
            throw new InvalidSignupException(AuthErrorMessage.NAME_TOO_LONG);
        }
        if (email == null || email.isBlank()) {
            throw new InvalidSignupException(AuthErrorMessage.EMAIL_REQUIRED);
        }
        if (email.length() > 255) {
            throw new InvalidSignupException(AuthErrorMessage.EMAIL_TOO_LONG);
        }
        if (password == null || password.isBlank()) {
            throw new InvalidSignupException(AuthErrorMessage.PASSWORD_REQUIRED);
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidSignupException(AuthErrorMessage.PASSWORD_TOO_LONG);
        }
    }
}
