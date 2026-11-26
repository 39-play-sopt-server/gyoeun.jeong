package org.sopt.member.message;

public enum MemberSuccessMessage {

    RETRIEVED("회원 정보를 조회했어요.");

    private final String message;

    MemberSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
