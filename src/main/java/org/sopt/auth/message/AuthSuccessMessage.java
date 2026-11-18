package org.sopt.auth.message;

public enum AuthSuccessMessage {
    SIGNED_UP("회원가입이 완료됐어요."),
    LOGGED_IN("로그인이 완료됐어요.");

    private final String message;

    AuthSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
