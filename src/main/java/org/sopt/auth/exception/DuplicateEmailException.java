package org.sopt.auth.exception;

import org.sopt.auth.message.AuthErrorMessage;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super(AuthErrorMessage.EMAIL_ALREADY_EXISTS.getMessage());
    }
}
