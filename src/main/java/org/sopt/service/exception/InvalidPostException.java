package org.sopt.service.exception;

public class InvalidPostException extends IllegalArgumentException {
    public InvalidPostException(String message) {
        super(message);
    }
}