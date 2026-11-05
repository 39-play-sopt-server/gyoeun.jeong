package org.sopt.member.service;

import org.sopt.member.domain.Member;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.exception.InvalidMemberException;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.message.MemberErrorMessage;
import org.sopt.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MemberResponse createMember(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidMemberException(MemberErrorMessage.NAME_REQUIRED);        }

        String normalizedName = name.strip();

        if (normalizedName.length() > 50) {
            throw new InvalidMemberException(MemberErrorMessage.NAME_TOO_LONG);
        }

        Member member = new Member(normalizedName);
        Member savedMember = repository.save(member);

        return MemberResponse.from(savedMember);
    }

    @Transactional
    public void deleteMember(long memberId) {
        if (memberId <= 0) {
            throw new InvalidMemberException(MemberErrorMessage.INVALID_MEMBER_ID);
        }

        Member member = repository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.withdraw();
    }
}