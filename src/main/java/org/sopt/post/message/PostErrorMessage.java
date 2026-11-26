package org.sopt.post.message;

public enum PostErrorMessage {
    POST_NOT_FOUND("게시글이 존재하지 않아요!"),
    NOT_AUTHOR("게시글 작성자만 수정하거나 삭제할 수 있어요."),
    TITLE_REQUIRED("제목이 비어있어요!"),
    CONTENT_REQUIRED("본문이 비어있어요!"),
    CATEGORY_REQUIRED("카테고리를 선택해 주세요!"),
    MEMBER_ID_REQUIRED("작성자 회원 ID를 입력해 주세요!");

    private final String message;

    PostErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
