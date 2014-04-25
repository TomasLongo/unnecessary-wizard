package de.tlongo.unneccesarywizard.java.core;

/**
 * Created by tolo on 16.04.2014.
 */
public interface Configuration {
    String getConfigName();
    String getConfigType();

    /**
     * Return the number of injection targets in this
     * configuration
     *
     * @return The number of injection targets
     */
    int getInjectionTargetCount();
}
