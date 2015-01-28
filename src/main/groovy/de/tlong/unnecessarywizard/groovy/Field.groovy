package de.tlong.unnecessarywizard.groovy

import de.tlongo.unneccesarywizard.java.core.Configuration

/**
 * Created by tomas on 28.01.15.
 */


class Field implements de.tlongo.unneccesarywizard.java.core.Field {
    def name
    def value
    def scope

    @Override
    String getName() {
        return name
    }

    @Override
    Object getValue() {
        return value
    }

    @Override
    Configuration.InjectionTarget.Scope getScope() {
        return scope
    }


    @Override
    public String toString() {
        return "Field{" +
                "name=" + name +
                ", value=" + value +
                ", scope=" + scope +
                '}';
    }
}