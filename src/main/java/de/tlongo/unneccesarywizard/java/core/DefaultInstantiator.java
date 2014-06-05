package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by tolo on 10.05.2014.
 */
public class DefaultInstantiator extends ClassInstantiator {
    @Override
    public Object instantiate(String name) throws InstantiationException {
        try {
            Class classToInstantiate;
            if (StringUtils.contains(name, ".")) {
                classToInstantiate = Class.forName(name);
            } else {
                throw new InstantiationException(String.format("Could not create class with unqualified name (%s)", name));
            }

            return classToInstantiate.newInstance();
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        } catch (java.lang.InstantiationException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        } catch (IllegalAccessException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        }
    }
}
