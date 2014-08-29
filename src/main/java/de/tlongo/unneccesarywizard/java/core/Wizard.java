package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

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

    Map<Configuration.InjectionTarget.InjectionMethod, InjectionMethod> injectionMethods;

    Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClass(Wizard.class)).
                                                                         setScanners(new ResourcesScanner(), new SubTypesScanner()));

    public Wizard() {
        // Search the classpath for 'wizard.groovy'

        Set<URL> urlsToDefaultConfig = ClasspathHelper.forResource("wizard.groovy", Wizard.class.getClassLoader());
        if (urlsToDefaultConfig.size() != 1) {
            throw new RuntimeException("Problems fetching default config. Found " + urlsToDefaultConfig.size() + " configs");
        }

        URL url = (URL)(urlsToDefaultConfig.toArray()[0]);
        logger.info(String.format("Found default config at %s", url.getPath()));
        injectionConfig = evaluateConfigScript(url.getPath() + "wizard.groovy");

        //injectionConfig = evaluateConfigScript();
        injectionMethods = new HashMap<>();

        injectionMethods.put(Configuration.InjectionTarget.InjectionMethod.SETTER, new SetterInjector());
        injectionMethods.put(Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR, new ConstructorInjector());
    }

    public Wizard(String configFile) {
        logger.info("Initializing the wizard with config file: " + configFile);

        injectionConfig = evaluateConfigScript(configFile);

        injectionMethods = new HashMap<>();

        injectionMethods.put(Configuration.InjectionTarget.InjectionMethod.SETTER, new SetterInjector());
        injectionMethods.put(Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR, new ConstructorInjector());
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
        GroovyClassLoader groovyLoader = new GroovyClassLoader(Wizard.class.getClassLoader());

        try {
            Class groovyDslRuntimeClass = groovyLoader.loadClass("de.tlong.unnecessarywizard.groovy.GroovyDSLRuntime");
            DSLRuntime dsl = (DSLRuntime)groovyDslRuntimeClass.newInstance();

            return dsl.createConfig(script);
        } catch (IllegalAccessException | java.lang.InstantiationException | InstantiationException e) {
            logger.error("An error occured evaluating the config script", e);
            throw new RuntimeException("An error occured evaluating the config script", e);
        } catch (ClassNotFoundException e) {
            logger.error("An error occured evaluating the config script", e);
            throw new RuntimeException("An error occured evaluating the config script", e);
        }
    }

    public Configuration getInjectionConfig() {
        return injectionConfig;
    }

    public Object createObjectGraph(String id) throws IllegalAccessException, InstantiationException {
        logger.debug(String.format("creating object graph for target: ", id));

        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget(id);
        if (target == null) {
            throw new IllegalArgumentException(String.format("Could not find InjectionTarget for class ", id));
        }

        //determine the injectionMethod for the injection target
        if (!injectionMethods.containsKey(target.getInjectionMethod())) {
            throw new RuntimeException(String.format("Unknown injection method '%s' for target '%s", target.getInjectionMethod(), target.getId()));
        }

        injectionMethod = injectionMethods.get(target.getInjectionMethod());
        return injectionMethod.performInjection(target);
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
    private Class findSingleImplementationOfInterface(Field field) {
        // The config says that only one implementation of the interface exists.
        // Check it...
        List<Class> implementations = new ArrayList<>();
        implementations.addAll(reflections.getSubTypesOf((Class)field.getType()));
        if (implementations.size() > 1) {
            throw new RuntimeException(String.format("Can not inject into field %s. Found more than one implementation for type %s", field.getName(), field.getType().getName()));
        }
        if (implementations.size() == 0) {
            throw new RuntimeException(String.format("Can not inject into field %s. Could not find implementation for type %s", field.getName(), field.getType().getName()));
        }

        return (Class)(implementations.get(0));
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
