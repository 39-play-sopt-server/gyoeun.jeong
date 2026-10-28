package org.sopt.member.message;

public enum MemberSuccessMessage {

    CREATED("회원을 생성했어요."),
    DELETED("회원 탈퇴가 완료됐어요.");

    private final String message;

    MemberSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}