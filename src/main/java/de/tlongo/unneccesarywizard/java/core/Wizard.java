package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Created by tolo on 16.04.2014.
 */
public class Wizard {
    private Configuration injectionConfig;

    public Wizard(String configFile) {
        System.out.println("Loading config file " + configFile);
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
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Configuration getInjectionConfig() {
        return injectionConfig;
    }

    public <T> T createObjectGraph(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        // Get injection target for passed class
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget(clazz.getName());
        if (target == null) {
            throw new IllegalArgumentException("Could not find injectionTarget for class " + clazz.getName());
        }

        final T targetObject = clazz.newInstance();

        // Inject values into fields of target
        target.getFields().forEach((k, v) -> {
            //Check for the type of the field using reflection
            String fieldName = k;
            try {
                Field field = clazz.getDeclaredField(fieldName);
                if (field.getType() == String.class) {
                    boolean isAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(targetObject, v);
                    field.setAccessible(isAccessible);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return targetObject;
    }
}
