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

    @Column(nullable = false)
    private Instant expiresAt;

    protected RefreshToken() {
    }

    public RefreshToken(String tokenHash, Long memberId, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.memberId = memberId;
        this.expiresAt = expiresAt;
    }

    public Long getMemberId() {
        return memberId;
    }
}
