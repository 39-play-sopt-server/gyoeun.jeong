package org.sopt.post.exception;

import org.sopt.post.message.PostErrorMessage;

public class PostForbiddenException extends RuntimeException {
    public PostForbiddenException() {
        super(PostErrorMessage.NOT_AUTHOR.getMessage());
    }
}
