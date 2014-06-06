package de.tlong.unnecessarywizard.groovy

import org.slf4j.LoggerFactory


/**
 * Created by tolo on 15.04.2014.
 */

public class InjectionTarget implements de.tlongo.unneccesarywizard.java.core.Configuration.InjectionTarget {
    def logger = LoggerFactory.getLogger(InjectionTarget.class)

    //fully qualified targetName ot the class which expects injections
    def targetName;

    //The targetName of the fields, which should be injected
    def fieldList = new FieldList()

    void targetName(name) {
        targetName = name
    }

    /**
     * Used to add fields and their injection values to the target
     *
     * @param fieldName
     * @param args
     *
     * @return
     */
    def invokeMethod(String methodName, args) {
        def errorMSg = "'$methodName' is not allowed in the injection target section"
        logger.error(errorMSg)
        throw new RuntimeException(errorMSg)
    }

    void fields(Closure closure) {
        logger.debug("setting delegate of closure passed to the field section to fieldList")
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this.fieldList
        closure()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        InjectionTarget that = (InjectionTarget) o

        if (fieldList != that.fieldList) return false
        if (logger != that.logger) return false
        if (targetName != that.targetName) return false

        return true
    }

    int hashCode() {
        int result
        result = (logger != null ? logger.hashCode() : 0)
        result = 31 * result + (targetName != null ? targetName.hashCode() : 0)
        result = 31 * result + (fieldList != null ? fieldList.hashCode() : 0)
        return result
    }

    @Override
    String getName() {
        return targetName
    }

    Map<String, Object> getFields() {
        return fieldList.getFields();
    }

}
