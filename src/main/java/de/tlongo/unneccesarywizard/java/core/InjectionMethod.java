package de.tlongo.unneccesarywizard.java.core;

import java.lang.reflect.Field;

/**
 * Created by tolo on 08.05.2014.
 */
public interface InjectionMethod {
    void inject(Object target, Object value, String fieldName);
}
