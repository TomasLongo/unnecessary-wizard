package de.tlong.unnecessarywizard.groovy


/**
 * Created by tolo on 15.04.2014.
 */

public class InjectionTarget implements de.tlongo.unneccesarywizard.java.core.Configuration.InjectionTarget {
    //fully qualified targetName ot the class which expects injections
    def targetName;

    //The targetName of the fields, which should be injected
    def fields = [:]

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
    def invokeMethod(String fieldName, args) {
        fields[fieldName] = args[0]
    }

    void fields(closure) {
        closure.delegate = this
        closure()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        InjectionTarget that = (InjectionTarget) o

        if (fields != that.fields) return false
        if (targetName != that.targetName) return false

        return true
    }

    int hashCode() {
        int result
        result = targetName.hashCode()
        result = 31 * result + fields.hashCode()
        return result
    }

    @Override
    public String toString() {
        return "InjectionTarget{" +
                "fields=" + fields +
                ", targetName=" + targetName +
                '}';
    }

    @Override
    String getName() {
        return targetName
    }

    Map<String, Object> getFields() {
        return fields;
    }

}
