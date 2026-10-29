package org.sopt.member.exception;

import org.sopt.member.message.MemberErrorMessage;

public class InvalidMemberException extends RuntimeException {

    public InvalidMemberException(MemberErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}