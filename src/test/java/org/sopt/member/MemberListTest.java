package org.sopt.member;

import org.junit.jupiter.api.Test;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRole;
import org.sopt.member.repository.MemberRepository;
import org.sopt.member.service.MemberService;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberListTest {
    @Test
    void listsMembersWithoutPasswordsAndDefaultsOldMembersToUser() {
        MemberRepository repository = mock(MemberRepository.class);
        Member member = new Member("이름", "user@example.com", "secret-hash");
        ReflectionTestUtils.setField(member, "role", null);
        when(repository.findAll()).thenReturn(List.of(member));

        var result = new MemberService(repository).getMembers();

        assertEquals(MemberRole.ROLE_USER, result.get(0).role());
        assertEquals("user@example.com", result.get(0).email());
        assertFalse(new ObjectMapper().valueToTree(result.get(0)).has("password"));
    }
}
