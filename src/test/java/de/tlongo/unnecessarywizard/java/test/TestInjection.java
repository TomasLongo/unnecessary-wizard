package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import de.tlongo.unneccesarywizard.java.core.Configuration;
import de.tlongo.unneccesarywizard.java.core.Wizard;
import de.tlongo.unnecessarywizard.java.test.objects.SimpleStringInjection;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by tolo on 15.04.2014.
 */
public class TestInjection {
    private static org.apache.commons.configuration.Configuration config;
    private final String TESTSCRIPT = "testconfig.groovy";

    @BeforeClass
    public static void setup() throws ConfigurationException {
        config = new PropertiesConfiguration("testconfig.properties");
    }
    @Test
    public void testInjectionConfig() {
        Wizard wizard = new Wizard(config.getString("resources.baseuri") + TESTSCRIPT);

        Configuration injectionConfig = wizard.getInjectionConfig();

        assertThat(injectionConfig, notNullValue());
        assertThat(injectionConfig.getConfigName(), equalTo("SuperInjector"));
        assertThat(injectionConfig.getConfigType(), equalTo("Debug"));
        assertThat(injectionConfig.getInjectionTargetCount(), equalTo(3));

        List<Configuration.InjectionTarget> targetList = injectionConfig.getInjectionTargets();
        assertThat(targetList, notNullValue());
        assertThat(targetList, hasSize(3));

        //Test single injection target
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget("ClassA");
        assertThat(target, notNullValue());
        assertThat(target.getName(), equalTo("ClassA"));

        Map<String, String> injectableFields = target.getFields();
        assertThat(injectableFields.size(), equalTo(1));
        assertThat(injectableFields.get("fieldNameOne"), equalTo("classToInject"));
    }

    @Test
    public void testInjection() throws InstantiationException, IllegalAccessException {
        Wizard wizard = new Wizard(config.getString("resources.baseuri") + "stringinjection.groovy");

        SimpleStringInjection object = wizard.createObjectGraph(SimpleStringInjection.class);

        assertThat(object, notNullValue());
        assertThat(object.getFieldToInject(), equalTo("This string was injected"));
        assertThat(object.getSecondField(),   equalTo("If this works..."));
        assertThat(object.getThirdField(),    equalTo("I go nuts"));
    }
}
