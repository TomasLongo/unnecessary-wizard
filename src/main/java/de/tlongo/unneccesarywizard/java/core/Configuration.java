package de.tlongo.unneccesarywizard.java.core;

import java.util.List;
import java.util.Map;

/**
 * Created by tolo on 16.04.2014.
 */

/**
 * Aggregatio of rules describing how objects should be wired together.
 */
public interface Configuration {
    public static interface InjectionTarget {
        /**
         * Returns the name of this target
         */
        String getId();

        /**
         * Returns the fully qualified class of this injection target
         */
        String getClassName();

        /**
         * Return the fields of this target, that should be
         * injected.
         *
         * @return
         */
        Map<String, Object> getFields();
    }

    String getConfigName();
    String getConfigType();

    /**
     * Return the number of injection targets in this
     * configuration
     *
     * @return The number of injection targets
     */
    int getInjectionTargetCount();

    /**
     * Gets all injection targets registered with this configuration.
     */
    List<InjectionTarget> getInjectionTargets();

    /**
     * Gets a specific injection target registered with this configuration
     *
     * @param name The of the injection target.
     *
     * @return An injection target found under the passed name.
     *         Null otherwise.
     */
    InjectionTarget getInjectionTarget(String name);
}
