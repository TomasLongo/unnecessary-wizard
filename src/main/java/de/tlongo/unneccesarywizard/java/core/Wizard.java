package de.tlongo.unneccesarywizard.java.core;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;

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
}
