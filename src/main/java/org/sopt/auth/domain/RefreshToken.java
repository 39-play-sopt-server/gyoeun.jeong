package org.sopt.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @Column(length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Long memberId;

    @Column(unique = true, length = 36)
    private String sessionId;

    @Column(nullable = false)
    private Instant expiresAt;

    protected RefreshToken() {
    }

    public RefreshToken(String tokenHash, Long memberId, String sessionId, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.memberId = memberId;
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
