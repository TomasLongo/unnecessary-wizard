package de.tlongo.unnecessarywizard.groovy.test

import de.tlong.unnecessarywizard.groovy.DSL
import de.tlong.unnecessarywizard.groovy.InjectionConfig

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test

/**
 * Created by tolo on 15.04.2014.
 */


public class TestDsl {
    final static String RESOURCE_BASE_URI = 'src\\test\\resources\\'
    @Test
    public void testConfigCreation() {
        InjectionConfig config = DSL.createFromScript(RESOURCE_BASE_URI + 'testconfig.groovy')

        assertThat('Config was not created.', config, notNullValue())
        assertThat(config.name, equalTo("SuperInjector"))
        assertThat(config.type, equalTo("Debug"))

        assertThat(config.packagesToScan, hasSize(greaterThan(0)))
        assertThat(config.packagesToScan, hasItems(equalTo("package.A"), equalTo("package.B")))

        assertThat(config.injectionTargetList, hasSize(3))
    }
}