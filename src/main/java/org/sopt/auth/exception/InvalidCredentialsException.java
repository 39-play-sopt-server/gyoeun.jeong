package org.sopt.auth.exception;

import org.sopt.auth.message.AuthErrorMessage;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super(AuthErrorMessage.INVALID_CREDENTIALS.getMessage());
    }
}
