package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

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

    Reflections reflections = new Reflections("de.tlongo.unnecessarywizard");

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

            if (isFieldPrimitive(field) || isFieldString(field)) {
                // Just inject the value as is if the field is primitive
                injectionMethod.inject(targetObject, value, fieldName);
            } else {
                // We have a complex object here.
                // Grab an instance and inject it.
                if (value instanceof String) {
                    // The field is a complex type and the value is a string telling us what to inject
                    Object objectToInject = null;


                    if (!field.getType().isInterface()) {
                        logger.debug(String.format("Field %s of type %s is not an interface.", field.getName(), field.getType().getName()));
                        objectToInject = instantiator.instantiate((String)value);
                    } else {
                        logger.debug(String.format("Field %s of type %s is an interface.", field.getName(), field.getType().getName()));
                        // We field we are injecting into is an interface
                        if (StringUtils.isEmpty((String)value)) {
                            // The config says that only one implementation of the interface exists.
                            // Check it...
                            Set<Class<?>> implementations = reflections.getSubTypesOf((Class)field.getType());
                            if (implementations.size() > 1) {
                                throw new RuntimeException(String.format("Can not inject into field %s of target %s. Found more than one implementation for type %s", fieldName, targetName, field.getType().getName()));
                            }
                            if (implementations.size() == 0) {
                                throw new RuntimeException(String.format("Can not inject into field %s of target %s. Could not find implementation for type %s", fieldName, targetName, field.getType().getName()));
                            }
                            // Instantiate the found impl
                            // TODO Do we have to construct a loop here just to get the single value out of the set.
                            for (Class klass : implementations) {
                                logger.debug(String.format("Injecting simple implementation (%s) of interface %s", klass.getName(), field.getType().getName()));

                                objectToInject = instantiator.instantiate(klass);
                            }
                        } else {
                            // The impl was provided in the config. Just instantiate it.
                            objectToInject = instantiator.instantiate((String)value);
                        }
                    }

                    injectionMethod.inject(targetObject, objectToInject, fieldName);
                } else {
                    // The value is an already instantiated type -> inject it directly
                    injectionMethod.inject(targetObject, value, fieldName);
                }
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
                klass.isPrimitive());
    }
}
