package de.tlong.unnecessarywizard.groovy

import org.slf4j.LoggerFactory

/**
 * Created by tolo on 05.06.2014.
 */

/**
 * Convienence class to give the closure of the "fields" section a delegate to run in.
 */
class FieldList {
    def logger = LoggerFactory.getLogger(FieldList.class)
    def fields = [:]

    def invokeMethod(String fieldName, args) {
        def fieldValue = args[0]
        logger.debug("Adding field: $fieldName with value: $fieldValue")
        fields[fieldName] = fieldValue
    }

    def getFields() {
        return fields
    }
}