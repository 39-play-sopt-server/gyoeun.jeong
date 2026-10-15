package org.sopt.post.message;

public enum PostSuccessMessage {
    CREATED("게시글을 작성했어요."),
    LISTED("게시글 목록을 조회했어요."),
    FOUND("게시글을 조회했어요."),
    UPDATED("게시글을 수정했어요."),
    DELETED("게시글을 삭제했어요.");

    private final String message;

    PostSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}