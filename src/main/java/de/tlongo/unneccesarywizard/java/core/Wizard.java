package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by tolo on 16.04.2014.
 */
public class Wizard {
    private Configuration injectionConfig;

    Logger logger = LoggerFactory.getLogger(Wizard.class);

    public Wizard(String configFile) {
        logger.info("Initializing the wizard with config file: " + configFile);

        injectionConfig = evaluateConfigScript(configFile);
    }

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

                //Check for the type of the field using reflection
                if (field.getType() == String.class) {
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(targetObject, v);
                    field.setAccessible(isAccessible);
                }
            } catch (NoSuchFieldException e) {
                logger.error("Field " + fieldName + "could not be found in target " + targetName);
            } catch (IllegalAccessException e) {
                logger.error("Not allowed to access field " + fieldName);
            }
        });

        return targetObject;
    }
}
