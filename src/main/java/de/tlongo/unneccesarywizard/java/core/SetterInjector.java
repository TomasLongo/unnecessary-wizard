package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tolo on 08.05.2014.
 */
public class SetterInjector implements InjectionMethod {
    Logger logger = LoggerFactory.getLogger(SetterInjector.class);

    /**
     * Sets the field of an object by invoking the appropriate setter on the object.
     *
     * Throws a NoSuchMethodException if the object´s class doesnt declare a setter
     * for the field.
     *
     * TODO Param field is only used to provide the type for the retrieval of the method. Maybe the method can do this by itself by providing the field name
     *
     *
     * @param targetObject    The object, which´s field should be set.
     * @param fieldName The name of the field that should be set.
     * @param value     The new value of the field.
     *
     * @throws NoSuchMethodException
     */
    @Override
    public void inject(Object targetObject, Object value, String fieldName) {
        String methodName = "set" + StringUtils.capitalize(fieldName);
        try {
            Class klass = targetObject.getClass();
            Field field = klass.getDeclaredField(fieldName);
            logger.debug(String.format("Injecting %s into %s via method %s", field.getType().getName(), klass.getName(), methodName));
            Method setterMethod = targetObject.getClass().getDeclaredMethod(methodName, field.getType());

            if (isFieldPrimitive(field)) {
                //Just inject the primitive object as is
                setterMethod.invoke(targetObject, value);
            } else {
                //We have a complex object here.
                if (! (value instanceof String)) {
                    //At this point we have an already instantiated object.
                    //Just inject it.
                    logger.info("Object to inject is already instantiated. Wizard will just inject it.");
                    setterMethod.invoke(targetObject, value);
                } else {
                    // The config contains just the qualified name of the object.
                    // Wizard has to get it´s class and instantiate it.
                    String qualifiedName = (String)value;
                    logger.debug(String.format("Instantiatin class to inject: %s", qualifiedName));
                    Object injectionValue = Class.forName(qualifiedName).newInstance();
                    setterMethod.invoke(targetObject, injectionValue);
                }
            }
        } catch (IllegalAccessException e) {
            logger.error(String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
        } catch (InvocationTargetException e) {
            logger.error(String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Could not create value to inject %s", (String)value), e);
        } catch (InstantiationException e) {
            logger.error(String.format("Could not create value to inject %s", (String)value), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a field´s type is primitve.
     *
     * NOTE: Strings are treated as primitve as well.
     * TODO Is the above mechanic good????
     *
     * @param field The field, which´s type should be checked.
     *
     * @return True if the type of the passed field is primitive.
     *         False otherwise.
     */
    private boolean isFieldPrimitive(Field field) {
        Class klass = field.getType();
        return (klass == Integer.class  ||
                klass == Double.class   ||
                klass == Long.class     ||
                klass == Float.class    ||
                klass == String.class   ||
                klass.isPrimitive());
    }
}
