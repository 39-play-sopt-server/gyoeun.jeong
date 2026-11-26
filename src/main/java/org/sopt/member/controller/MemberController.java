package org.sopt.member.controller;

import org.sopt.common.dto.ApiResponse;
import org.sopt.member.message.MemberSuccessMessage;
import org.sopt.member.dto.MemberListResponse;
import org.sopt.member.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<MemberListResponse>> getMembers() {
        return ApiResponse.success(MemberSuccessMessage.LISTED.getMessage(), service.getMembers());
    }

    @GetMapping("/me")
    public ApiResponse<Long> me(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(MemberSuccessMessage.RETRIEVED.getMessage(), memberId);
    }
}
