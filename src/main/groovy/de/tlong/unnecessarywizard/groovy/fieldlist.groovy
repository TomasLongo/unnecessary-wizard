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
        def interfaces = args[0].getClass().getInterfaces()

        def infoMap = args[0]
        def field = new Field()
        field.name = fieldName
        if (interfaces.length == 1 && interfaces[0] == Map.class) {
            field.scope = infoMap.scope
            field.value = infoMap.value
        } else {
            field.value = args[0]
            field.scope = Configuration.InjectionTarget.Scope.INSTANCE
        }

        logger.debug("Adding field: {}", field.toString())
        fields[fieldName] = field
    }

    def getFields() {
        return fields
    }
}