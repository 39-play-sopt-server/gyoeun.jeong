package org.sopt.post.exception;

import org.sopt.post.message.PostErrorMessage;

public class InvalidPostException extends RuntimeException {
    public InvalidPostException(PostErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
