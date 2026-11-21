
package com.aerova.exception;

public class AirportException extends Exception {

    public AirportException(String message) {
        super(message);
    }

    public AirportException(String message, Throwable cause) {
        super(message, cause);
    }
}
