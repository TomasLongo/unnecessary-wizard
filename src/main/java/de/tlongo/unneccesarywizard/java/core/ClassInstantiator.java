package de.tlongo.unneccesarywizard.java.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;

/**
 * Created by tolo on 10.05.2014.
 */
public abstract class ClassInstantiator {
    static Marker logMarker = MarkerFactory.getMarker("Wizard");
    protected Logger logger = LoggerFactory.getLogger(ClassInstantiator.class);

    public abstract Object instantiate(String name) throws InstantiationException;

    static final String ERROR_MESSAGE_FORMAT_STRING = "Could not create instance from class %s";

    public Object instantiate(Class klass) throws InstantiationException {
        try {
            return klass.newInstance();
        } catch (java.lang.InstantiationException e) {
            logger.error(logMarker, ERROR_MESSAGE_FORMAT_STRING, klass.getName());
            throw new RuntimeException(String.format(ERROR_MESSAGE_FORMAT_STRING, klass.getName()));
        } catch (IllegalAccessException e) {
            logger.error(logMarker, ERROR_MESSAGE_FORMAT_STRING, klass.getName());
            throw new RuntimeException(String.format(ERROR_MESSAGE_FORMAT_STRING, klass.getName()));
        }
    }
}
