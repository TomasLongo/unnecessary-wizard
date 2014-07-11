package de.tlongo.unneccesarywizard.java.core;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Created by tolo on 08.07.2014.
 */
public class ConstructorInjector implements InjectionMethod {
    private Reflections reflections = new Reflections("de.tlongo.unnecessarywizard");

    @Override
    public void inject(Object target, Object value, String fieldName) {
        throw new NotImplementedException();
    }
}
