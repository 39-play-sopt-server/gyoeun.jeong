package org.sopt.post.message;

public enum PostErrorMessage {
    POST_NOT_FOUND("게시글이 존재하지 않아요!"),
    TITLE_REQUIRED("제목이 비어있어요!"),
    CONTENT_REQUIRED("본문이 비어있어요!"),
    CATEGORY_REQUIRED("카테고리를 선택해 주세요!"),
    AUTHOR_REQUIRED("작성자가 비어있어요!"),
    INVALID_REQUEST_BODY("본문 형식이나 카테고리 값을 확인해주세요!"),
    INVALID_POST_ID("게시글 ID는 숫자로 입력해 주세요!");

    private final String message;

    PostErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}