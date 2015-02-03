package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;

/**
 * Created by tolo on 10.05.2014.
 */
public class DefaultInstantiator extends ClassInstantiator {
    static Marker logMarker = MarkerFactory.getMarker("Wizard");
    static private String ERROR_MESSAGE_FORMAT_STRING = "Could not create object with name %s";

    @Override
    public Object instantiate(String name) throws InstantiationException {
        try {
            Class classToInstantiate;
            if (StringUtils.contains(name, ".")) {
                classToInstantiate = Class.forName(name);
            } else {
                logger.error(logMarker, String.format("Could not create class with unqualified name (%s)", name));
                throw new InstantiationException(String.format("Could not create class with unqualified name (%s)", name));
            }

            return classToInstantiate.newInstance();
        } catch (ClassNotFoundException e) {
            logger.error(logMarker, String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
            throw new InstantiationException(String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
        } catch (java.lang.InstantiationException e) {
            logger.error(logMarker, String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
            throw new InstantiationException(String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
        } catch (IllegalAccessException e) {
            logger.error(logMarker, String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
            throw new InstantiationException(String.format(ERROR_MESSAGE_FORMAT_STRING, name), e);
        }
    }
}
