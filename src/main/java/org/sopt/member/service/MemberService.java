package org.sopt.member.service;

import org.sopt.member.dto.MemberListResponse;
import org.sopt.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<MemberListResponse> getMembers() {
        return repository.findAll().stream().map(MemberListResponse::from).toList();
    }
}
