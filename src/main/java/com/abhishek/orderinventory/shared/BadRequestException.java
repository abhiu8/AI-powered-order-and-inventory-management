package com.abhishek.orderinventory.shared;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
