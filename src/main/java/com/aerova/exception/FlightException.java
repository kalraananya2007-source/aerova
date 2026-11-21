
package com.aerova.exception;

public class FlightException extends Exception {

    private static final long serialVersionUID = 1L;

    // Constructor with error message
    public FlightException(String message) {
        super(message);
    }

    // Constructor with error message and original cause
    public FlightException(String message, Throwable cause) {
        super(message, cause);
    }
}
