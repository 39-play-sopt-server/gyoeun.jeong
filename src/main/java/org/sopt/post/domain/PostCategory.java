package org.sopt.post.domain;

public enum PostCategory {
    FREE("자유"),
    QUESTION("질문"),
    INFO("정보"),
    REVIEW("후기");

    private final String displayName;

    PostCategory(String displayName) {
        this.displayName = displayName;
    }}