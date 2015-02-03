package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

/**
 * Created by tolo on 05.06.2014.
 */

/**
 * Convienence class to give the closure of the "fields" section a delegate to run in.
 */
class FieldList {
    static def logger = LoggerFactory.getLogger(FieldList.class)

    // [Sting:Field]
    def fields = [:]

    def invokeMethod(String fieldName, args) {
        // We have to check if the script provides just the
        // value to inject or if there is some additional information.
        //
        // e.g. fieldName(value:"lala", scope:SINGLETON)
        //
        // The second case is realized with named params. Groovy creates
        // a single map in this case. We check if the
        // argument implements an interface, and if so, if the interface
        // is of type `Map`


        def interfaces = args[0].getClass().getInterfaces()

        def field = new Field()
        field.name = fieldName
        if (interfaces.length == 1 && interfaces[0] == Map.class) {
            def infoMap = args[0]
            field.scope = infoMap.scope
            field.value = infoMap.value
        } else {
            // Just the value is provided. Scope default is `SINGELTON`
            field.value = args[0]
            field.scope = Configuration.InjectionTarget.Scope.INSTANCE
        }

        logger.debug("Adding field: {}", field.toString())
        fields[fieldName] = field
    }

    def getFields() {
        return fields
    }

    def getField(name) {
        return fields[name]
    }
}