package org.sopt.auth.exception;

import org.sopt.auth.message.AuthErrorMessage;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super(AuthErrorMessage.INVALID_REFRESH_TOKEN.getMessage());
    }
}
