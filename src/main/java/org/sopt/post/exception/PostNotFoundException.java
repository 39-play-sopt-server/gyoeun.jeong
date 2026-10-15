package org.sopt.post.exception;

import org.sopt.post.message.PostErrorMessage;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super(PostErrorMessage.POST_NOT_FOUND.getMessage());
    }
}