package com.thecommitcrew.domain.exception;

/**
 * Thrown when an account attempts to withdraw or transfer more
 * money than the available cash balance.
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

}
