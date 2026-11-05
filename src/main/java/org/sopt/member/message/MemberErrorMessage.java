package org.sopt.member.message;

public enum MemberErrorMessage {

    MEMBER_NOT_FOUND("활동 중인 회원을 찾을 수 없어요."),
    NAME_REQUIRED("회원 이름을 입력해 주세요."),
    NAME_TOO_LONG("회원 이름은 50자 이하로 입력해 주세요."),
    INVALID_MEMBER_ID("회원 ID는 양수로 입력해 주세요.");

    private final String message;

    MemberErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}