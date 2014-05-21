package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by tolo on 15.04.2014.
 */

//Script script = new GroovyShell().parse(new File("testscript.groovy"))

public class InjectionConfig implements Configuration {
    String name
    String type
    def injectionTargetList = [:]
    def packages = []

    Logger logger = LoggerFactory.getLogger(InjectionConfig.class)

    def name(String name) {
        logger.debug("setting name for config to $name")
        this.name = name
    }

    def injectionTarget(closure) {

        InjectionTarget target = new InjectionTarget()
        closure.delegate = target;
        closure()

        logger.debug("Creating injection target ${target.name}")

        injectionTargetList[target.name] = target
    }

    def invokeMethod(String methodName, args) {
        if (methodName == "packagesToScan") {
            logger.debug('processing packages...')
            println args[0]
            def packagesList = args[0]
            packagesList.each { item ->
                packagesToScan << item
            }
        } else if (methodName == "type") {
            type = args[0]
        } else {
            logger.warning("Unknown property found: ${methodName}")
        }
    }

    def String toString() {
        def dump = new StringBuffer()
        dump << "Dumping InjectionConfig\n"
        dump << "targetName:${name}\n"
        dump << "type:${type}\n"
        dump << "packages to scan\n"
        packagesToScan.each {item ->
            dump << item + "\n"
        }
        injectionTargetList.each {
            dump << it.toString() + "\n"
        }

        return dump.toString()
    }

    @Override
    String getConfigName() {
        return name
    }

    @Override
    String getConfigType() {
        return type
    }

    @Override
    int getInjectionTargetCount() {
        return injectionTargetList.size()
    }

    @Override
    List<Configuration.InjectionTarget> getInjectionTargets() {
        return injectionTargetList.values().toList();
    }

    @Override
    Configuration.InjectionTarget getInjectionTarget(String name) {
        return injectionTargetList[name];
    }

    @Override
    List<String> getPackagesToScan() {
        return packages;
    }
}

//config = new InjectionConfig()
//
//script.metaClass.injector = {
//    Closure cl -> cl.setDelegate(config)
//        cl()
//        println config.toString()
//}
//
//script.run()