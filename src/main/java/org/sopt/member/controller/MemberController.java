package org.sopt.member.controller;

import org.sopt.common.dto.ApiResponse;
import org.sopt.member.dto.CreateMemberRequest;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.message.MemberSuccessMessage;
import org.sopt.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> createMember(
            @RequestBody CreateMemberRequest request
    ) {
        MemberResponse response = service.createMember(request.name());

        return ApiResponse.success(
                MemberSuccessMessage.CREATED.getMessage(),
                response
        );
    }

    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteMember(
            @PathVariable("memberId") long memberId
    ) {
        service.deleteMember(memberId);

        return ApiResponse.success(
                MemberSuccessMessage.DELETED.getMessage(),
                null
        );
    }
}