package com.example.demo.exception;

/** Порушення бізнес-правила. RuntimeException => @Transactional відкотить транзакцію. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
