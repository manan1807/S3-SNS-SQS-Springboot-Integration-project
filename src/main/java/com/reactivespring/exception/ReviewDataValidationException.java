package com.reactivespring.exception;

public class ReviewDataValidationException extends RuntimeException {
    private String message;
    public ReviewDataValidationException(String s) {
        super(s);
        this.message=s;
    }
}
