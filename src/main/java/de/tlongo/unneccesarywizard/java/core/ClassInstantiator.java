package de.tlongo.unneccesarywizard.java.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tolo on 10.05.2014.
 */
public abstract class ClassInstantiator {
    protected Logger logger = LoggerFactory.getLogger(ClassInstantiator.class);

    public abstract Object instantiate(String name) throws InstantiationException;

    public Object instantiate(Class klass) throws InstantiationException {
        try {
            return klass.newInstance();
        } catch (java.lang.InstantiationException e) {
            throw new RuntimeException(String.format("Could not create instance from class %s", klass.getName()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Could not create instance from class %s", klass.getName()));
        }
    }
}
