package com.goganesh.packages.exception;

public class StopActiveProcessException extends RuntimeException{
    public StopActiveProcessException(String message) {
        super(message);
    }
}
