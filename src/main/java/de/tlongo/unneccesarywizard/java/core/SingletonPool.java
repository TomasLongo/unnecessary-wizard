package de.tlongo.unneccesarywizard.java.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomas on 03.02.15.
 */
public class SingletonPool {
    Logger logger = LoggerFactory.getLogger(SingletonPool.class);

    public Map<String, Object> pool = new HashMap<>();
    ClassInstantiator instantiator;

    public SingletonPool(ClassInstantiator instantiator) {
        this.instantiator = instantiator;
    }

    /**
     * Returns the object denoted by className.
     *
     * If no such Object exists, it will be instantiated and cached
     * for further access.
     *
     * NOTE: The SingletonPool assumes that the config file was properly parsed
     *       *and* evaluated in advance. No checks are performed to proof that
     *       objects requested here are already instantiated somewhere else with
     *       a scope other than `SINGLETON`.
     *
     * @param className The fully qualified classname
     */
    public Object getSingleton(String className) {
        logger.debug("Fetching {} from singelton pool", className);
        if (!pool.containsKey(className)) {
            logger.debug("{} has to be instantiated first.", className);
            Object singleton = instantiator.instantiate(className);
            pool.put(className, singleton);

            return singleton;
        }

        return pool.get(className);
    }
}
