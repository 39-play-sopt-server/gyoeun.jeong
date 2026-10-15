package org.sopt.post.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super(PostErrorMessage.POST_NOT_FOUND.getMessage());
    }
}