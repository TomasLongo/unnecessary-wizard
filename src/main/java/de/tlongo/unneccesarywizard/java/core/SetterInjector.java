package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tolo on 08.05.2014.
 */
public class SetterInjector implements InjectionMethod {
    static Marker logMarker = MarkerFactory.getMarker("Wizard");
    static Logger logger = LoggerFactory.getLogger(SetterInjector.class);

    ClassInstantiator instantiator = new DefaultInstantiator();

    Reflections reflections = new Reflections("de.tlongo.unnecessarywizard");

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
    private void injectIntoField(Object targetObject, Object value, String fieldName) {
        String methodName = "set" + StringUtils.capitalize(fieldName);
        try {
            Class klass = targetObject.getClass();
            Field field = klass.getDeclaredField(fieldName);
            logger.info(logMarker, String.format("Injecting %s into %s via method %s", field.getType().getName(), klass.getName(), methodName));

            Method setterMethod = targetObject.getClass().getDeclaredMethod(methodName, field.getType());

            setterMethod.invoke(targetObject, value);
        } catch (IllegalAccessException e) {
            logger.error(logMarker, String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
            throw new RuntimeException(String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
        } catch (InvocationTargetException e) {
            logger.error(logMarker, String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
            throw new RuntimeException(String.format("Error invoking the method %s on class %s", methodName, targetObject.getClass().getName()), e);
        } catch (InstantiationException e) {
            logger.error(logMarker, String.format("Could not create value to inject %s", (String)value), e);
            throw new RuntimeException(String.format("Could not create value to inject %s", (String)value), e);
        } catch (NoSuchMethodException e) {
            logger.error(logMarker, String.format("Method %s is not declared in class %s", methodName, targetObject.getClass().getName()));
            throw new RuntimeException(String.format("Method %s is not declared in class %s", methodName, targetObject.getClass().getName()));
        } catch (NoSuchFieldException e) {
            logger.error(logMarker, String.format("Field %s is not declared in class %s", fieldName, targetObject.getClass().getName()));
            throw new RuntimeException(String.format("Field %s is not declared in class %s", fieldName, targetObject.getClass().getName()));
        }
    }

    private Field getFieldFromClass(Class klass, String fieldName) {
        try {
            return klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.error(logMarker, String.format("Field %s is not declared in class %s", fieldName, klass.getName()));
            throw new RuntimeException(String.format("Field %s is not declared in class %s", fieldName, klass.getName()));
        }
    }

    @Override
    public Object performInjection(Configuration.InjectionTarget target) {
        logger.info(logMarker, String.format("Using setter injection for target %s", target.getId()));
        final Object targetObject = instantiator.instantiate(target.getClassName());
        Class clazz = targetObject.getClass();
        // Inject values into fields of target
        target.getFields().forEach((fieldName, configField) -> {
            Field declaredField = getFieldFromClass(clazz, fieldName);

            if (isFieldPrimitive(declaredField) || isFieldString(declaredField)) {
                // Just inject the value as is if the field is primitive
                injectIntoField(targetObject, configField.getValue(), fieldName);
            } else {
                // We have a complex object here.
                // Grab an instance and inject it.
                if (configField.getValue() instanceof String) {
                    // The field is a complex type and the value is a string telling us what to inject
                    Object objectToInject = null;


                    if (!declaredField.getType().isInterface()) {
                        logger.debug(String.format("Field %s of type %s is not an interface.", declaredField.getName(), declaredField.getType().getName()));
                        objectToInject = instantiator.instantiate((String)configField.getValue());
                    } else {
                        logger.debug(String.format("Field %s of type %s is an interface.", declaredField.getName(), declaredField.getType().getName()));
                        // We field we are injecting into is an interface
                        if (StringUtils.isEmpty((String)configField.getValue())) {
                            // The config says that only one implementation of the interface exists.
                            // Check it...
                            Class klass = checkAndFindSingleImplementationOfInterface(declaredField);
                            objectToInject = instantiator.instantiate(klass);
                        } else {
                            // The impl was provided in the config. Just instantiate it.
                            objectToInject = instantiator.instantiate((String)configField.getValue());
                        }
                    }

                    injectIntoField(targetObject, objectToInject, fieldName);
                } else {
                    // The value is an already instantiated type -> inject it directly
                    injectIntoField(targetObject, configField.getValue(), fieldName);
                }
            }
        });

        return targetObject;
    }

    /**
     * Looks for an implementation of an interface for the passed field.
     *
     * This method is intended to be used when the value for an interface is
     * omited in the configuration. In that case, the wizard checks if there
     * exists an implementation for that interface. In case it finds more than
     * one implementation, the wizard throws an Exception since it is unable to
     * decide which one to choose. The user has to specify it then.
     *
     * @param field The field, into which the implementation should be injected.
     *              The underlying type must be an interface.
     *
     * @return      The class object of the found implementation.
     */
    private Class checkAndFindSingleImplementationOfInterface(Field field) {
        // The config says that only one implementation of the interface exists.
        // Check it...
        List<Class> implementations = new ArrayList<>();
        implementations.addAll(reflections.getSubTypesOf((Class)field.getType()));
        if (implementations.size() > 1) {
            logger.error(logMarker, String.format("Can not injectIntoField into field %s. Found more than one implementation for type %s", field.getName(), field.getType().getName()));
            throw new RuntimeException(String.format("Can not injectIntoField into field %s. Found more than one implementation for type %s", field.getName(), field.getType().getName()));
        }
        if (implementations.size() == 0) {
            logger.error(logMarker, String.format("Can not injectIntoField into field %s. Could not find implementation for type %s", field.getName(), field.getType().getName()));
            throw new RuntimeException(String.format("Can not injectIntoField into field %s. Could not find implementation for type %s", field.getName(), field.getType().getName()));
        }

        return (Class)(implementations.get(0));
    }

    private boolean isFieldString(Field field) {
        return field.getType() == String.class;
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