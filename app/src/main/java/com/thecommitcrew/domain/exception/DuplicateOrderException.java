package com.thecommitcrew.domain.exception;

/**
 * Thrown when an attempt is made to place a duplicate order.
 */
public class DuplicateOrderException extends Exception {

    public DuplicateOrderException(String message) {
        super(message);
    }

    public DuplicateOrderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}