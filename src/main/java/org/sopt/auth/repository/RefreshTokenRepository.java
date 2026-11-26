package org.sopt.auth.repository;

import jakarta.persistence.LockModeType;
import org.sopt.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token where token.tokenHash = :hash")
    Optional<RefreshToken> findByHashForUpdate(@Param("hash") String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token where token.sessionId = :sessionId and token.memberId = :memberId")
    Optional<RefreshToken> findSessionForUpdate(@Param("sessionId") String sessionId, @Param("memberId") Long memberId);

    boolean existsBySessionIdAndMemberId(String sessionId, Long memberId);

    void deleteByMemberId(Long memberId);

    void deleteByExpiresAtBefore(Instant now);
}
