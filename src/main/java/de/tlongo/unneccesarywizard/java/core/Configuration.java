package de.tlongo.unneccesarywizard.java.core;

import java.util.List;
import java.util.Map;

/**
 * Created by tolo on 16.04.2014.
 */
public interface Configuration {
    public static interface InjectionTarget {
        /**
         * Return the name of this target
         *
         * @return
         */
        String getName();

        /**
         * Return the fields of this target, that should be
         * injected.
         *
         * @return
         */
        Map<String, String> getFields();
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

    List<InjectionTarget> getInjectionTargets();

    InjectionTarget getInjectionTarget(String name);
}
