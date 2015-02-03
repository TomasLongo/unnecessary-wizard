package de.tlongo.unneccesarywizard.java.core;

/**
 * Represents a certain mechanic to inject values into fields.
 *
 * Created by Tomas Longo on 08.05.2014.
 */
public abstract class Injector {
    SingletonPool singletonPool;

    ClassInstantiator instantiator;

    public Injector(SingletonPool singletonPool, ClassInstantiator instantiator) {
        this.singletonPool = singletonPool;
        this.instantiator = instantiator;
    }

    /**
     * Performs injections according to the rules defined in the passed InjectionTarget
     *
     * @param target The target, that provides the rules for the injection
     *
     * @return A Java object, that is initialised according to the specs of the injection target.
     */
    public abstract Object performInjection(Configuration.InjectionTarget target);
}
