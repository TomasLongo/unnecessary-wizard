package de.tlongo.unnecessarywizard.java.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import de.tlongo.unneccesarywizard.java.core.*;
import de.tlongo.unneccesarywizard.java.core.InstantiationException;
import de.tlongo.unnecessarywizard.java.test.objects.*;
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

    @Test(expected = RuntimeException.class)
    public void testConfigErrorInTargetSectionWrongInjectionMethod() throws Exception {
        try {
            Wizard wizard = createWizard("testconfigerrorinjectionmethod.groovy");
        } catch (RuntimeException e) {
            logger.error("An error occured while parsing the config file", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInjectionConfig() {
        // create wizard with default config 'wizard.groovy'
        Wizard wizard = new Wizard();

        Configuration injectionConfig = wizard.getInjectionConfig();

        assertThat(injectionConfig, notNullValue());
        assertThat(injectionConfig.getConfigName(), equalTo("SuperInjector"));
        assertThat(injectionConfig.getConfigType(), equalTo("Debug"));
        assertThat(injectionConfig.getInjectionTargetCount(), equalTo(4));

        List<Configuration.InjectionTarget> targetList = injectionConfig.getInjectionTargets();
        assertThat(targetList, notNullValue());
        assertThat(targetList, hasSize(4));

        //Test single injection target
        Configuration.InjectionTarget target = injectionConfig.getInjectionTarget("MyClassA");
        assertThat(target, notNullValue());
        assertThat(target.getId(), equalTo("MyClassA"));
        assertThat(target.getClassName(), equalTo("my.package.ClassA"));

        Map<String, Object> injectableFields = target.getFields();
        assertThat(injectableFields.size(), equalTo(1));
        assertThat(injectableFields.get("fieldNameOne"), equalTo("classToInject"));

        Configuration.InjectionTarget constructorTarget = injectionConfig.getInjectionTarget("ConstructorInjection");
        assertThat(constructorTarget, notNullValue());
        assertThat(constructorTarget.getInjectionMethod(), is(Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR));
        List<Object> constructorParams = constructorTarget.getConstructorParams();
        assertThat(constructorParams, hasSize(2));
        assertThat(constructorParams, contains( equalTo(new String("stringParam")), equalTo(new Float(23.00))));
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

    @Test
    public void testConstructorInjection() throws Exception {
        Wizard wizard = createWizard("ctorinjection.groovy");
        CtorInjection ctorInjection = (CtorInjection) wizard.createObjectGraph("CtorInjection");

        assertThat(ctorInjection, notNullValue());
        assertThat(ctorInjection.getField(), equalTo("string"));
        assertThat(ctorInjection.getSingleInterface(), notNullValue());
        assertThat(ctorInjection.getSingleInterface().singleMethod(), equalTo("This is the impl of the single interface"));
        assertThat(ctorInjection.getFloatValue(), is(23.00f));
        assertThat(ctorInjection.getDoubleValue(), is(23.00));
        assertThat(ctorInjection.getIntValue(), is(23));
        assertThat(ctorInjection.getLongValue(), is(23L));
        assertThat(ctorInjection.getBd(), equalTo(new BigDecimal("23.00")));

        assertThat(ctorInjection.getSampleClassToInject(), notNullValue());
        assertThat(ctorInjection.getSampleClassToInject().getField(), is(23));
    }
}
