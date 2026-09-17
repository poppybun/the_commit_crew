package com.thecommitcrew.domain.exception;

public class NegativePriceException extends Exception {
   public NegativePriceException(String message) {
       super(message);
   }
}