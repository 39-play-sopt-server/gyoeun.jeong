package org.sopt.auth.exception;

import org.sopt.auth.message.AuthErrorMessage;

public class InvalidSignupException extends RuntimeException {
    public InvalidSignupException(AuthErrorMessage message) {
        super(message.getMessage());
    }
}
