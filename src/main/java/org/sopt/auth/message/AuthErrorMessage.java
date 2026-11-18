package org.sopt.auth.message;

public enum AuthErrorMessage {
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED("인증이 필요하거나 토큰이 유효하지 않습니다.");

    private final String message;

    AuthErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
