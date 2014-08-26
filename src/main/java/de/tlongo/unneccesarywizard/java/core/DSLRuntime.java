package de.tlongo.unneccesarywizard.java.core;

/**
 * Created by tolo on 16.04.2014.
 */

/**
 * The wizard uses this interface to get a Configuration
 * in order to work his injection magic.
 */
public interface DSLRuntime {
    Configuration createConfig(String configFile);
}
