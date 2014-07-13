package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration
import org.slf4j.LoggerFactory


/**
 * Created by Tomas Longo on 15.04.2014.
 */

public class InjectionTarget implements de.tlongo.unneccesarywizard.java.core.Configuration.InjectionTarget {
    def logger = LoggerFactory.getLogger(InjectionTarget.class)

    def id;

    //fully qualified name of the underlying class
    def className

    //The targetName of the fields, which should be injected
    def fieldList = new FieldList()

    // The method that is used to inject values
    // Default is field injection
    def injectionMethod = Configuration.InjectionTarget.InjectionMethod.SETTER

    def constructorParamsArray = [] as ArrayList<Object>;

    void id(id) {
        this.id = id
    }

    def className(className) {
        this.className = className
    }

    def constructorParams(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    def injectionMethod(injectionMethod) {
        logger.debug("setting injection method to $injectionMethod")
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
            constructorParamsArray << args[0]
        } else {
            def errorMSg = "'$methodName' is not allowed in the injection target section"
            logger.error(errorMSg)
            throw new RuntimeException(errorMSg)
        }
    }

    void fields(Closure closure) {
        logger.debug("setting delegate of closure passed to the field section to fieldList")
        if (injectionMethod == Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR) {
            throw new RuntimeException("Error parsing injection section. Can not inject via setter/field when " +
                    "injection method is set 'Constructor")
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
    Map<String, Object> getFields() {
        return fieldList.getFields();
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
