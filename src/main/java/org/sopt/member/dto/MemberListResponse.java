package org.sopt.member.dto;

import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRole;

import java.time.LocalDateTime;

public record MemberListResponse(Long id, String name, String email, MemberRole role,
                                 LocalDateTime createdAt, LocalDateTime deletedAt) {
    public static MemberListResponse from(Member member) {
        return new MemberListResponse(member.getId(), member.getName(), member.getEmail(), member.getRole(),
                member.getCreatedAt(), member.getDeletedAt());
    }
}
