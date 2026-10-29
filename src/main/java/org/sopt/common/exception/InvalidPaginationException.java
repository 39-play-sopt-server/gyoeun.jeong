package org.sopt.common.exception;

import org.sopt.common.message.CommonErrorMessage;

public class InvalidPaginationException extends RuntimeException {

    public InvalidPaginationException(CommonErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
