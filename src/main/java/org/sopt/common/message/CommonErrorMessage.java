package org.sopt.common.message;

public enum CommonErrorMessage {

    INVALID_REQUEST_BODY("요청 본문 형식을 확인해 주세요."),
    INVALID_PARAMETER("요청 값의 자료형이나 허용 값을 확인해 주세요.");

    private final String message;

    CommonErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
