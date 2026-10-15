package org.sopt.post.exception;

public class InvalidPostException extends RuntimeException {
    public InvalidPostException(PostErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
