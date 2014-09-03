package de.tlong.unnecessarywizard.groovy

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
    def fields = [:]

    def invokeMethod(String fieldName, args) {
        def fieldValue = args[0]
        logger.info(MarkerFactory.getMarker("Wizard"), "Adding field: $fieldName with value: $fieldValue")
        fields[fieldName] = fieldValue
    }

    def getFields() {
        return fields
    }
}