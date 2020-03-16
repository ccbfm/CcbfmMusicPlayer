package com.ccbfm.music.player.exception;

public class ChildPlaceException extends RuntimeException {

    public ChildPlaceException() {
    }

    public ChildPlaceException(String message) {
        super(message);
    }

    public ChildPlaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
