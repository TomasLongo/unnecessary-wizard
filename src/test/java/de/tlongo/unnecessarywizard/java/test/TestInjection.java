package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import de.tlongo.unneccesarywizard.java.core.Wizard;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by tolo on 15.04.2014.
 */
public class TestInjection {
    private static Configuration config;
    private final String TESTSCRIPT = "testconfig.groovy";

    @BeforeClass
    public static void setup() throws ConfigurationException {
        config = new PropertiesConfiguration("testconfig.properties");
    }
    @Test
    public void testInjection() {
        Wizard wizard = new Wizard(config.getString("resources.baseuri") + TESTSCRIPT);

        de.tlongo.unneccesarywizard.java.core.Configuration injectionConfig =
                wizard.getInjectionConfig();

        assertThat(injectionConfig, notNullValue());
        assertThat(injectionConfig.getConfigName(), equalTo("SuperInjector"));
        assertThat(injectionConfig.getConfigType(), equalTo("Debug"));
    }
}
