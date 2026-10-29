package org.sopt.member.exception;

import org.sopt.member.message.MemberErrorMessage;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super(MemberErrorMessage.MEMBER_NOT_FOUND.getMessage());
    }
}