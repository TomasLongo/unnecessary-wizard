package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import de.tlongo.unneccesarywizard.java.core.DSLRuntime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by tolo on 15.04.2014.
 */


class GroovyDSLRuntime implements DSLRuntime {
    static Logger logger = LoggerFactory.getLogger(GroovyDSLRuntime.class)

    public static Configuration createFromScript(String scriptName) {
        logger.info("Creating configuration from script " + scriptName)
        InjectionConfig config = new InjectionConfig()

        Script script = new GroovyShell().parse(new File(scriptName))
        script.metaClass.injector = { Closure cl ->
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl.delegate = config
            cl()
        }
        script.run()

        return (Configuration)config
    }

    @Override
    Configuration createConfig(String configFile) {
        return createFromScript(configFile);
    }
}