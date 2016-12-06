package de.ruf2.rube.fridgeorganizer.exception;

/**
 * Created by Bernhard Ruf on 11.10.2016.
 */
public class UnknownModelException extends RuntimeException {

    public UnknownModelException() {
    }

    public UnknownModelException(String message) {
        super(message);
    }
}