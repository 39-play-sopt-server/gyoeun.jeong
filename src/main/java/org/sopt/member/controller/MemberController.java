package org.sopt.member.controller;

import org.sopt.common.dto.ApiResponse;
import org.sopt.member.message.MemberSuccessMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    @GetMapping("/me")
    public ApiResponse<Long> me(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(MemberSuccessMessage.RETRIEVED.getMessage(), memberId);
    }
}
