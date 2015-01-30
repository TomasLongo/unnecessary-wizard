package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

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

    /**
     * Creates a wizard with the default config named 'wizard.groovy'
     *
     * The config must be in the classpath. If none, or more than config is found
     * an excpetion is thrown.
     */
    public Wizard() {
        // Search the classpath for 'wizard.groovy'
        Set<URL> urlsToDefaultConfig = ClasspathHelper.forResource("wizard.groovy", Wizard.class.getClassLoader());
        if (urlsToDefaultConfig.size() != 1) {
            throw new RuntimeException("Problems fetching default config. Found " + urlsToDefaultConfig.size() + " configs");
        }

        URL url = (URL)(urlsToDefaultConfig.toArray()[0]);
        logger.info("Wizard", "Found default config at %s\n", url.getPath());
        injectionConfig = evaluateConfigScript(url.getPath() + "wizard.groovy");

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

            Configuration config = dsl.createConfig(script);

            final Set<String> singletonScope = new HashSet<>();
            final Set<String> instanceScope = new HashSet<>();
            config.getInjectionTargets().forEach(target -> {
                logger.debug("Evaluating target {}", target.getId());
                // Parse all fields to inject and check if we have some violations
                // regarding the scopes
                target.getFields().forEach((key, field) -> {
                    logger.debug("Evaluating field {}", field.toString());

                    if (field.getValue() instanceof String) {
                        logger.debug("Candidate...");
                        if (field.getScope().equals(Configuration.InjectionTarget.Scope.INSTANCE)) {
                            if (singletonScope.contains(field.getValue())) {
                                logger.error("Error parsing script. Target {} contains field with name {} that is injected " +
                                        "with scope INSTANCE but was previously injected with scope SINGLETON.",
                                        target.getId(), field.getName());
                                throw new RuntimeException("Error parsing config");
                            }
                            instanceScope.add((String)field.getValue());
                        } else if (field.getScope().equals(Configuration.InjectionTarget.Scope.SINGLETON)) {
                            if (instanceScope.contains(field.getValue())) {
                                logger.error("Error parsing script. Target {} contains field with name {} that is injected " +
                                                "with scope SINGLETON but was previously injected with scope INSTANCE.",
                                        target.getId(), field.getName());
                                throw new RuntimeException("Error parsing config");
                            }
                            singletonScope.add((String)field.getValue());
                        }
                    }
                });
            });

            return config;
        } catch (IllegalAccessException | java.lang.InstantiationException | InstantiationException e) {
            logger.error("Wizard", "An error occured evaluating the config script", e);
            throw new RuntimeException("An error occured evaluating the config script", e);
        } catch (ClassNotFoundException e) {
            logger.error("Wizard", "An error occured evaluating the config script", e);
            throw new RuntimeException("An error occured evaluating the config script", e);
        }
    }

    public Configuration getInjectionConfig() {
        return injectionConfig;
    }

    public Object createObjectGraph(String id) throws IllegalAccessException, InstantiationException {
        logger.debug("Wizard", "creating object graph for target: ", id);

        // Extract the injectiontarget from the config
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget(id);
        if (target == null) {
            logger.error("Wizard", "Could not find InjectionTarget for class %s\n", id);
            throw new IllegalArgumentException(String.format("Could not find InjectionTarget for class %s\n", id));
        }

        //Determine the injectionMethod for the injection target
        if (!injectionMethods.containsKey(target.getInjectionMethod())) {
            logger.error("Wizard", "Unknown injection method '%s' for target '%s\n", target.getInjectionMethod(), target.getId());
            throw new RuntimeException(String.format("Unknown injection method '%s' for target '%s", target.getInjectionMethod(), target.getId()));
        }

        injectionMethod = injectionMethods.get(target.getInjectionMethod());
        return injectionMethod.performInjection(target);
    }
}
