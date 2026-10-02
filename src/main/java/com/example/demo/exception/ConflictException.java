package com.example.demo.exception;

/** Конфлікт із поточним станом даних (наприклад, видалення групи, в якій є студенти). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
