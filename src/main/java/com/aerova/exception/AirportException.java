
package com.aerova.exception;

public class AirportException extends Exception {

    private static final long serialVersionUID = 1L;

    public AirportException(String message) {
        super(message);
    }

    public AirportException(String message, Throwable cause) {
        super(message, cause);
    }
}
