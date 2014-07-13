package de.tlongo.unneccesarywizard.java.core;

import java.lang.reflect.Field;

/**
 * Represents a certain mechanic to inject values into fields.
 *
 * Created by Tomas Longo on 08.05.2014.
 */
public interface InjectionMethod {
    /**
     * Performs injections according to the rules defined in the passed InjectionTarget
     *
     * @param target The target, that provides the rules for the injection
     *
     * @return A Java object, that is initialised according to the specs of the injection target.
     */
    default Object performInjection(Configuration.InjectionTarget target) {
        return null;
    }
}
