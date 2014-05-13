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

    InjectionMethod injectionMethod;
    ClassInstantiator instantiator;

    public Wizard(String configFile) {
        logger.info("Initializing the wizard with config file: " + configFile);

        injectionConfig = evaluateConfigScript(configFile);
    }

    public void setInjectionMethod(InjectionMethod method) {
        this.injectionMethod = method;
    }

    public void setInstantiator(ClassInstantiator instantiator) {
        this.instantiator = instantiator;
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
        } catch (IOException | IllegalAccessException | java.lang.InstantiationException | InstantiationException e) {
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

        // TODO Find target by simple name or qualified name
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget(targetName);
        if (target == null) {
            throw new IllegalArgumentException("Could not find InjectionTarget for class " + targetName);
        }

        final T targetObject = (T)instantiator.instantiate(clazz);

        // Inject values into fields of target
        target.getFields().forEach((fieldName, value) -> {
            //TODO What is the type of 'value' here?
            Field field = getFieldFromClass(clazz, fieldName);

            // TODO This is bullshit!! If the field is primitive or NOT a string!?!?
            if (isFieldPrimitive(field) || !(value instanceof String)) {
                // Just inject the value as is if the field is primitive
                injectionMethod.inject(targetObject, value, fieldName);
            } else {
                // We have a complex object here.
                // Grab an instance and inject it.
                Object objectToInject = instantiator.instantiate((String)value);
                injectionMethod.inject(targetObject, objectToInject, fieldName);
            }
        });

        return targetObject;
    }

    /**
     * Gets a declared field from a class.
     *
     * @param klass
     * @param fieldName
     * @return
     */
    private Field getFieldFromClass(Class klass, String fieldName) {
        try {
            return klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("Could not get field %s from class %s: no such field", fieldName, klass.getName()));
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
