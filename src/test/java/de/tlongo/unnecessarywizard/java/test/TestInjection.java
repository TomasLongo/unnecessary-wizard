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

import java.math.BigDecimal;
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

    private Wizard createWizard(String scriptName) {
        Wizard wizard = new Wizard(config.getString("resources.baseuri") + scriptName);;
        wizard.setInjectionMethod(new SetterInjector());
        wizard.setInstantiator(new DefaultInstantiator(wizard.getInjectionConfig().getPackagesToScan()));
        return wizard;
    }

    @Test
    public void testInjectionConfig() {
        Wizard wizard = createWizard(TESTSCRIPT);

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

        Map<String, Object> injectableFields = target.getFields();
        assertThat(injectableFields.size(), equalTo(1));
        assertThat(injectableFields.get("fieldNameOne"), equalTo("classToInject"));

        List<String> packagesToScan = injectionConfig.getPackagesToScan();
        assertThat(packagesToScan, notNullValue());
        assertThat(packagesToScan, hasSize(2));
        assertThat(packagesToScan, allOf(hasItem("package.A"), hasItem("package.B")));
    }

    @Test
    public void testInjection() throws InstantiationException, IllegalAccessException {
        Wizard wizard = createWizard("stringinjection.groovy");

        SimpleStringInjection object = wizard.createObjectGraph(SimpleStringInjection.class);

        assertThat(object, notNullValue());
        assertThat(object.getFieldToInject(), equalTo("This string was injected"));
        assertThat(object.getSecondField(),   equalTo("If this works..."));
        assertThat(object.getThirdField(),    equalTo("I go nuts"));
    }

    @Test
    public void testPrimitiveInjection() throws Exception {
        Wizard wizard = createWizard("primitiveinjection.groovy");

        SimplePrimitiveInjection object = wizard.createObjectGraph(SimplePrimitiveInjection.class);

        assertThat(object, notNullValue());
        assertThat(object.getDoubleField(), closeTo(0.456789, 0.0000000001));
        assertThat((object.getFloatField() - 23.44f), lessThan(0.0000000001f));
        assertThat(object.getIntField(), equalTo(123));
        assertThat(object.getLongField(), equalTo(456L));
    }


    @Test
    public void testComplexObjectInjection() throws Exception {
        Wizard wizard = createWizard("complexinjection.groovy");

        ComplexObject object = wizard.createObjectGraph(ComplexObject.class);

        assertThat(object, notNullValue());

        assertThat(object.getDecimalValue(), notNullValue());
        assertThat(object.getDecimalValue(), equalTo(new BigDecimal("22.3")));

        assertThat(object.getList(), notNullValue());

        assertThat(object.getSampleClass(), notNullValue());
        assertThat(object.getSampleClass().getField(), equalTo(23));

        assertThat(object.getSingleInterface(), notNullValue());
        assertThat(object.getSingleInterface().singleMethod(), equalTo("This is the impl of the single interface"));
    }
}
