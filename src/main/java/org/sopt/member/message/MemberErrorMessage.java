package org.sopt.member.message;

public enum MemberErrorMessage {

    MEMBER_NOT_FOUND("활동 중인 회원을 찾을 수 없어요."),
    INVALID_MEMBER_ID("회원 ID는 양수로 입력해 주세요.");

    private final String message;

    MemberErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
