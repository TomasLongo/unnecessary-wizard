package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import de.tlongo.unneccesarywizard.java.core.DSLProcessor

/**
 * Created by tolo on 15.04.2014.
 */


class DSL implements DSLProcessor {
    public static Configuration createFromScript(String scriptName) {
        println 'starting to process config script'
        InjectionConfig config = new InjectionConfig()

        Script script = new GroovyShell().parse(new File(scriptName))
        script.metaClass.injector = {
            Closure cl -> cl.setDelegate(config)
                cl()
        }
        script.run()

        println "config: ${config}"
        return (Configuration)config
    }

    @Override
    Configuration createConfig(String configFile) {
        return createFromScript(configFile);
    }
}