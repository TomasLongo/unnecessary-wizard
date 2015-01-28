package de.tlongo.unneccesarywizard.java.core;

/**
 * Created by tomas on 28.01.15.
 */
public interface Field {
    String getName();
    Object getValue();
    Configuration.InjectionTarget.Scope getScope();
}
