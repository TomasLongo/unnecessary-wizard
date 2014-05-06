package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tolo on 16.04.2014.
 */

/**
 * The main actor of the DI-Container.
 *
 * Responsible for creating objects with all their dependencies
 * according to the rules of a Configuration.
 */
public class Wizard {
    private Configuration injectionConfig;

    Logger logger = LoggerFactory.getLogger(Wizard.class);

    public Wizard(String configFile) {
        logger.info("Initializing the wizard with config file: " + configFile);

        injectionConfig = evaluateConfigScript(configFile);
    }

    /**
     * Creates a configuration based on a config script.
     *
     * @param script The name of the config script.
     *
     */
    private Configuration evaluateConfigScript(String script) {
        ClassLoader javaLoader = getClass().getClassLoader();
        GroovyClassLoader groovyLoader = new GroovyClassLoader(javaLoader);

        try {
            Class groovyDslClass = groovyLoader.parseClass(new File("src\\main\\groovy\\de\\tlong\\unnecessarywizard\\groovy\\dsl.groovy"));
            DSLProcessor dsl = (DSLProcessor)groovyDslClass.newInstance();

            return dsl.createConfig(script);
        } catch (IOException e) {
            logger.error("An error occured evaluating the config script", e);
        } catch (InstantiationException e) {
            logger.error("An error occured evaluating the config script", e);
        } catch (IllegalAccessException e) {
            logger.error("An error occured evaluating the config script", e);
        }

        return null;
    }

    public Configuration getInjectionConfig() {
        return injectionConfig;
    }

    /**
     * Creates an instance of a class with all its dependencies.
     *
     * @param clazz The class of the object that should be created.
     * @param <T>   The type of the Object that should be created.
     *
     * @throws IllegalAccessException If a field of the class is not accessible
     * @throws InstantiationException If it was not possible to create an instance of T.
     */
    public <T> T createObjectGraph(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        String targetName = clazz.getName();
        logger.debug("creating object graph for target: " + targetName);
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget(targetName);
        if (target == null) {
            throw new IllegalArgumentException("Could not find InjectionTarget for class " + targetName);
        }

        final T targetObject = clazz.newInstance();

        // Inject values into fields of target
        target.getFields().forEach((k, v) -> {
            String fieldName = k;
            try {
                Field field = clazz.getDeclaredField(fieldName);

                //TODO What is the type of 'v' here? This is important for the following generic method in the next line.
                setFieldViaSetterInjection(targetObject, field, v);
                //setFieldViaReflection(targetObject, field, v);
            } catch (NoSuchFieldException e) {
                logger.error("Field " + fieldName + "could not be found in target " + targetName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });

        return targetObject;
    }

    /**
     * Sets a field of an object by directly accessing the field
     * via reflection.
     *
     * Throws an IllegalAccessException if the can not be accessed
     * via reflection.
     *
     * @param object    The object, which´s field should be set.
     * @param field     The field, that should be set.
     * @param value     The new value of the field.
     *
     * @throws IllegalAccessException
     */
    private void setFieldViaReflection(Object object, Field field, Object value) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(isAccessible);
    }

    /**
     * Sets the field of an object by invoking the appropriate setter on the object.
     *
     * Throws a NoSuchMethodException if the object´s class doesnt declare a setter
     * for the field.
     *
     * TODO Param field is only used to provide the type for the retrieval of the method. Maybe the method can this by itself by providing the field name
     *
     *
     * @param object The object, which´s field should be set.
     * @param field  The field, that should be set.
     * @param value  The new value of the field.
     * @param <T>    The type of the new value.
     *
     * @throws NoSuchMethodException
     */
    private <T> void setFieldViaSetterInjection(Object object, Field field, T value) throws NoSuchMethodException {
        String methodName = "set" + StringUtils.capitalize(field.getName());
        try {
            Method setterMethod = object.getClass().getDeclaredMethod(methodName, field.getType());
            setterMethod.invoke(object, value);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Error invoking the method %s on class %s", methodName, object.getClass().getName()), e);
        } catch (InvocationTargetException e) {
            logger.error(String.format("Error invoking the method %s on class %s", methodName, object.getClass().getName()), e);
        }
    }
}
