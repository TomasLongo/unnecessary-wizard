package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import de.tlongo.unneccesarywizard.java.core.Configuration.InjectionTarget.InjectionMethod
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Created by Tomas Longo on 15.04.2014.
 */

public class InjectionConfig implements Configuration {
    String name
    String type
    def injectionTargetList = [:]

    static Marker logMarker = MarkerFactory.getMarker("Wizard")
    static Logger logger = LoggerFactory.getLogger(InjectionConfig.class)

    def name(String name) {
        this.name = name
    }

    def injectionTarget(Closure closure) {
        InjectionTarget target = new InjectionTarget()
        closure.delegate = target;
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        logger.debug(logMarker, "Finished creating injection target ${target.id}")

        injectionTargetList[target.id] = target
    }

    def type(type) {
        this.type = type
    }

    def invokeMethod(String methodName, args) {
        logger.error(logMarker, "The property '$methodName' is not allowed inside a the config section")
        throw new RuntimeException("The property '$methodName' is not allowed inside a the config section")
    }

    def String toString() {
        def dump = new StringBuffer()
        dump << "Dumping InjectionConfig\n"
        dump << "targetName:${name}\n"
        dump << "type:${type}\n"
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
    Configuration.InjectionTarget getInjectionTarget(String id) {
        return injectionTargetList[id];
    }
}