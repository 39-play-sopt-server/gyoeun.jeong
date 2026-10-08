package org.sopt.service.exception;

public class InputClosedException extends RuntimeException {
    public InputClosedException() {
        super("입력이 종료되어 프로그램을 종료합니다.");
    }
}