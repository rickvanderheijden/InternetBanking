package com.ark.bankingapplication.exceptions;

/**
 *
 * @author Rick van der Heijden
 */
public class ControlNotLoadedException extends Exception {
    // Parameterless Constructor
    public ControlNotLoadedException() {
    }

    // Constructor that accepts a message
    public ControlNotLoadedException(String message) {
        super(message);
    }

    // Constructor that accepts an exception
    public ControlNotLoadedException(Exception exception) {
        super(exception);
    }
}
