package de.tlong.unnecessarywizard.groovy

/**
 * Created by tolo on 15.04.2014.
 */

public class InjectionTarget {
    //fully qualified name ot the class which expects injections
    def name;

    //The name of the fields, which should be injected
    def fields = [:]



    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        InjectionTarget that = (InjectionTarget) o

        if (fields != that.fields) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + fields.hashCode()
        return result
    }

    @Override
    public String toString() {
        return "InjectionTarget{" +
                "fields=" + fields +
                ", name=" + name +
                '}';
    }
}
