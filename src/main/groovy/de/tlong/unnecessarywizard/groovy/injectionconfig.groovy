package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration

/**
 * Created by tolo on 15.04.2014.
 */

//Script script = new GroovyShell().parse(new File("testscript.groovy"))

public class InjectionConfig implements Configuration {
    String name
    String type
    def injectionTargetList = [:]
    def packagesToScan = []

    def injectionTarget(targetName, fields) {
        println 'processing target'
        InjectionTarget target = new InjectionTarget()

        target.targetName = targetName
        fields.entrySet().each {
            println "adding '${it.key}:${it.value} to '${targetName}"
            target.fields[it.key] = it.value
        }

        injectionTargetList[targetName] = target
    }

    def invokeMethod(String methodName, args) {
        if (methodName == "name") {
            name = args[0]
        } else if (methodName == "packagesToScan") {
            println 'processing packages...'
            println args[0]
            def packagesList = args[0]
            packagesList.each { item ->
                packagesToScan << item
            }
        } else if (methodName == "type") {
            type = args[0]
        } else {
            println "Not defined method '${methodName}'i was called"
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