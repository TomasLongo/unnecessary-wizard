package de.tlongo.unneccesarywizard.java.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Created by tolo on 08.05.2014.
 */
public class FieldInjector implements InjectionMethod {
    Logger logger = LoggerFactory.getLogger(FieldInjector.class);

    public void inject(Object target, Object value, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(isAccessible);
        } catch (NoSuchFieldException e) {
            logger.error(String.format("Target object %s has no field named %s", target.getClass().getName(), fieldName), e);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Can not access field %s of class %s", fieldName, target.getClass().getName()));
        }
    }
}
