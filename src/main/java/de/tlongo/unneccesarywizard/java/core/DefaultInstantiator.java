package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by tolo on 10.05.2014.
 */
public class DefaultInstantiator extends ClassInstantiator {
    public DefaultInstantiator(List<String> packagesToScan) {
        super(packagesToScan);
    }

    @Override
    public Object instantiate(String name) throws InstantiationException {
        try {
            Class classToInstantiate;
            if (StringUtils.contains(name, ".")) {
                classToInstantiate = Class.forName(name);
            } else {
                if (packagesToScan == null) {
                    throw new InstantiationException(String.format("Could not create class with unqualified name (%s) since no packages were provided", name));
                }

                classToInstantiate = scanPackagesForClass(name);
            }

            if (classToInstantiate == null) {
                throw new InstantiationException(String.format("Could not create object with name %s", name));
            }

            return classToInstantiate.newInstance();
        } catch (ClassNotFoundException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        } catch (java.lang.InstantiationException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        } catch (IllegalAccessException e) {
            throw new InstantiationException(String.format("Could not create object with name %s", name), e);
        }
    }

    private Class scanPackagesForClass(String className) throws ClassNotFoundException {
        for (String p : packagesToScan) {
            StringBuilder builder = new StringBuilder(p);
            builder.append(".").append(className);

            try {
                return  Class.forName(builder.toString());
            } catch (ClassNotFoundException e) {
                logger.info(String.format("%s could not be found", builder.toString()));
            }
        }

        logger.error(String.format("%s could not be found in any package", className));

        return null;
    }

    private boolean isNameQualified(String name) {
        return StringUtils.contains(name, ".");
    }
}
