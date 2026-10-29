package org.sopt.common.message;

public enum CommonErrorMessage {

    INVALID_REQUEST_BODY("요청 본문 형식을 확인해 주세요."),
    INVALID_PARAMETER("요청 값의 자료형이나 허용 값을 확인해 주세요."),
    INVALID_PAGE("페이지 번호는 0 이상으로 입력해 주세요."),
    INVALID_PAGE_SIZE("페이지 크기는 1 이상 100 이하로 입력해 주세요."),
    PAGE_OFFSET_TOO_LARGE("요청한 페이지의 조회 범위가 너무 커요.");

    private final String message;

    CommonErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
