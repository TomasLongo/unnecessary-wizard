package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import de.tlongo.unneccesarywizard.java.core.*;
import de.tlongo.unneccesarywizard.java.core.InstantiationException;
import de.tlongo.unnecessarywizard.java.test.objects.ComplexObject;
import de.tlongo.unnecessarywizard.java.test.objects.SimplePrimitiveInjection;
import de.tlongo.unnecessarywizard.java.test.objects.SimpleStringInjection;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by tolo on 15.04.2014.
 */
public class TestInjection {
    Logger logger = LoggerFactory.getLogger(TestInjection.class);

    private static org.apache.commons.configuration.Configuration config;

    @BeforeClass
    public static void setup() throws ConfigurationException {
        config = new PropertiesConfiguration("testconfig.properties");
    }

    private Wizard createWizard(String scriptName) {
        Wizard wizard = new Wizard(config.getString("resources.baseuri") + scriptName);
        wizard.setInjectionMethod(new SetterInjector());
        wizard.setInstantiator(new DefaultInstantiator());
        return wizard;
    }

    @Test(expected = RuntimeException.class)
    public void testConfigErrorInConfigSection() throws Exception {
        try {
            Wizard wizard = createWizard("testconfigerror.groovy");
        } catch (RuntimeException e) {
            logger.error("An error occured while parsing the config file", e);
            throw new RuntimeException(e);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testConfigErrorInTargetSection() throws Exception {
        try {
            Wizard wizard = createWizard("testconfigerrortargetsection.groovy");
        } catch (RuntimeException e) {
            logger.error("An error occured while parsing the config file", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInjectionConfig() {
        Wizard wizard = createWizard("testconfig.groovy");

        Configuration injectionConfig = wizard.getInjectionConfig();

        assertThat(injectionConfig, notNullValue());
        assertThat(injectionConfig.getConfigName(), equalTo("SuperInjector"));
        assertThat(injectionConfig.getConfigType(), equalTo("Debug"));
        assertThat(injectionConfig.getInjectionTargetCount(), equalTo(3));

        List<Configuration.InjectionTarget> targetList = injectionConfig.getInjectionTargets();
        assertThat(targetList, notNullValue());
        assertThat(targetList, hasSize(3));

        //Test single injection target
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget("MyClassA");
        assertThat(target, notNullValue());
        assertThat(target.getId(), equalTo("MyClassA"));
        assertThat(target.getClassName(), equalTo("my.package.ClassA"));

        Map<String, Object> injectableFields = target.getFields();
        assertThat(injectableFields.size(), equalTo(1));
        assertThat(injectableFields.get("fieldNameOne"), equalTo("classToInject"));
    }

    @Test
    public void testInjection() throws InstantiationException, IllegalAccessException {
        Wizard wizard = createWizard("stringinjection.groovy");

        SimpleStringInjection object = (SimpleStringInjection)wizard.createObjectGraph("StringInjector");

        assertThat(object, notNullValue());
        assertThat(object.getFieldToInject(), equalTo("This string was injected"));
        assertThat(object.getSecondField(),   equalTo("If this works..."));
        assertThat(object.getThirdField(),    equalTo("I go nuts"));
    }

    @Test
    public void testPrimitiveInjection() throws Exception {
        Wizard wizard = createWizard("primitiveinjection.groovy");

        SimplePrimitiveInjection object = (SimplePrimitiveInjection)wizard.createObjectGraph("Simple");

        assertThat(object, notNullValue());
        assertThat(object.getDoubleField(), closeTo(0.456789, 0.0000000001));
        assertThat((object.getFloatField() - 23.44f), lessThan(0.0000000001f));
        assertThat(object.getIntField(), equalTo(123));
        assertThat(object.getLongField(), equalTo(456L));
    }


    @Test
    public void testComplexObjectInjection() throws Exception {
        Wizard wizard = createWizard("complexinjection.groovy");

        ComplexObject object = (ComplexObject)wizard.createObjectGraph("ComplexObject");

        assertThat(object, notNullValue());

        assertThat(object.getDecimalValue(), notNullValue());
        assertThat(object.getDecimalValue(), equalTo(new BigDecimal("22.3")));

        assertThat(object.getList(), notNullValue());

        assertThat(object.getSampleClass(), notNullValue());
        assertThat(object.getSampleClass().getField(), equalTo(23));

        assertThat(object.getSingleInterface(), notNullValue());
        assertThat(object.getSingleInterface().singleMethod(), equalTo("This is the impl of the single interface"));
    }

    @Test(expected = RuntimeException.class)
    public void testInterfaceInjectionError() throws Exception {
        // Test if wizard detects that there is more than one implementation
        // of an interface while the config doesnt specify which implementation
        // to choose.
        
        Wizard wizard = createWizard("complexinjectionerror.groovy");
        ComplexObject co = (ComplexObject)wizard.createObjectGraph("Failed");
    }
}
