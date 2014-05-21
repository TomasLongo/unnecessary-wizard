package de.tlongo.unneccesarywizard.java.core;

/**
 * Created by tolo on 12.05.2014.
 */
public class InstantiationException extends RuntimeException {
    public InstantiationException(String message) {
        super(message);
    }

    public InstantiationException(String message, Throwable e) {
        super(message, e);
    }

    public InstantiationException(Throwable e) {
        super(e);
    }
}
