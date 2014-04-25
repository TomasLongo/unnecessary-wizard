package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration

/**
 * Created by tolo on 15.04.2014.
 */

//Script script = new GroovyShell().parse(new File("testscript.groovy"))

public class InjectionConfig implements Configuration {
    String name
    String type
    def injectionTargets = []
    def packagesToScan = []

    def injectionTarget(targetName, fields) {
        println 'processing target'
        InjectionTarget target = new InjectionTarget()

        target.name = targetName
        fields.entrySet().each {
            println "adding '${it.key}:${it.value} to '${targetName}"
            target.fields[it.key] = it.value
        }

        injectionTargets << target
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
        dump << "name:${name}\n"
        dump << "type:${type}\n"
        dump << "packages to scan\n"
        packagesToScan.each {item ->
            dump << item + "\n"
        }
        injectionTargets.each {
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