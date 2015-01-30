package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory


/**
 * Created by Tomas Longo on 15.04.2014.
 */

public class InjectionTarget implements de.tlongo.unneccesarywizard.java.core.Configuration.InjectionTarget {
    Marker logMarker = MarkerFactory.getMarker("Wizard")
    static def logger = LoggerFactory.getLogger(InjectionTarget.class)

    def id;

    //fully qualified name of the underlying class
    def className

    //The targetName of the fields, which should be injected
    def fieldList = new FieldList()

    // The method that is used to inject values
    // Default is setter injection
    def injectionMethod = Configuration.InjectionTarget.InjectionMethod.SETTER

    // Only used when injection method is set to 'Constructor'
    def constructorParamsArray = [] as ArrayList<Object>;

    void id(id) {
        this.id = id
    }

    def className(className) {
        logger.info(logMarker, "Injection Target '$id' has underlying class'$className'")
        this.className = className
    }

    def constructorParams(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    def injectionMethod(injectionMethod) {
        logger.info(logMarker, "Setting injection method to '$injectionMethod' for target '$id' ")
        this.injectionMethod = injectionMethod
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
        def regex = ~/param\d+/
        if (regex.matcher(methodName).matches()) {
            // This section block handles the defs of contructor params
            // and adds them to a map
            logger.info(logMarker, "Adding parameter for constructor injection. Value: ${args[0]}")
            constructorParamsArray << args[0]
        } else {
            def errorMSg = "'$methodName' is not allowed in the injection target section"
            logger.error(errorMSg)
            throw new RuntimeException(errorMSg)
        }
    }

    void fields(Closure closure) {
        if (injectionMethod == Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR) {
            def errorMsg = "Error parsing injection section. Can not inject via setter/field when injection method is set 'Constructor'"
            logger.error(errorMsg)
            throw new RuntimeException(errorMsg)
        }
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this.fieldList
        closure()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        InjectionTarget that = (InjectionTarget) o

        if (className != that.className) return false
        if (fieldList != that.fieldList) return false
        if (id != that.id) return false
        if (injectionMethod != that.injectionMethod) return false
        if (logger != that.logger) return false

        return true
    }

    int hashCode() {
        int result
        result = (logger != null ? logger.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (className != null ? className.hashCode() : 0)
        result = 31 * result + (fieldList != null ? fieldList.hashCode() : 0)
        result = 31 * result + (injectionMethod != null ? injectionMethod.hashCode() : 0)
        return result
    }

    @Override
    String getId() {
        return id
    }

    @Override
    String getClassName() {
        return className
    }

    @Override
    Map<String, Field> getFields() {
        return fieldList.getFields();
    }

    @Override
    de.tlongo.unneccesarywizard.java.core.Field getField(String name) {
        return fieldList.getField(name)
    }

    @Override
    Configuration.InjectionTarget.InjectionMethod getInjectionMethod() {
        return injectionMethod
    }

    @Override
    List<Object> getConstructorParams() {
        return constructorParamsArray
    }
}
