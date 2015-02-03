package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.GStringImpl;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by tolo on 08.07.2014.
 */
public class ConstructorInjector extends Injector {
    static Marker logMarker = MarkerFactory.getMarker("Wizard");
    static Logger logger = LoggerFactory.getLogger(ConstructorInjector.class);

    private Reflections reflections = new Reflections("de.tlongo.unnecessarywizard");

    public ConstructorInjector(SingletonPool singletonPool, ClassInstantiator instantiator) {
        super(singletonPool, instantiator);
    }

    @Override
    public Object performInjection(Configuration.InjectionTarget target) {
        logger.info(logMarker, String.format("Using constructor injection for target %s", target.getId()));

        try {
            Class targetClass = Class.forName(target.getClassName());

            // The constructor itself
            Constructor ctor = findConstructorForClass(targetClass, target.getFields().size());

            // The parameters of the ctor as declared in the source
            final Class[] ctorParamTypes = ctor.getParameterTypes();

            // The evaluated parameters, e.g. instantiated classes
            final List<Object> evaluatedParams = new ArrayList<>();

            IntStream.range(0, ctorParamTypes.length).forEach(index -> {
                logger.info(logMarker, String.format("Injecting param%d into %s", index, target.getId()));

                // The value of the ctor parameter provided by the config
                String paramName = String.format("param%d", index+1);
                //Object ctorParam = target.getConstructorParams().get(index);
                Field ctorParam = target.getField(paramName);

                // the current ctor param to evaluate
                Class klass = ctorParamTypes[index];

                if (isTypePrimitive(klass) || isTypeString(klass)) {
                    logger.debug(logMarker, "Param is primitive");
                    evaluatedParams.add(ctorParam.getValue());
                } else if (klass.isInterface()) {
                    logger.debug(logMarker, "Param is interface");
                    // We have to inject an interface here
                    Class interfaceImpl;
                    if (StringUtils.isEmpty((String) ctorParam.getValue())) {
                        // The config says, that there is only one implementation.
                        // Check and find it...
                        interfaceImpl = checkAndFindSingleImplementationOfInterface(klass);
                        evaluatedParams.add(instantiator.instantiate(interfaceImpl));
                    } else {
                        // The implementation was provided in the config
                        evaluatedParams.add(instantiator.instantiate((String) ctorParam.getValue()));
                    }
                } else {
                    logger.debug(logMarker, "Param is object");

                    // Determine if the object was already instantiated in the script, or if the classname
                    // was provided. We can do this by simply checking if we encounter a string here.
                    if (ctorParam.getValue().getClass() == GStringImpl.class || isTypeString(ctorParam.getValue().getClass())) {
                        logger.debug(logMarker, "Instantiating {}", ctorParam.toString());
                        String classToInstatiate = ctorParam.getValue().toString();
                        if (ctorParam.getScope().equals(Configuration.InjectionTarget.Scope.SINGLETON)) {
                            evaluatedParams.add(singletonPool.getSingleton(classToInstatiate));
                        } else {
                            evaluatedParams.add(instantiator.instantiate(classToInstatiate));
                        }
                    } else {
                        logger.debug(logMarker, "Object is already instantiated in script");
                        evaluatedParams.add(ctorParam.getValue());
                    }
                }
            });
            return ctor.newInstance(evaluatedParams.toArray());
        } catch (java.lang.InstantiationException e) {
            logger.error(logMarker, "Could not instantiate type for constructor param", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error(logMarker, "Could not instantiate type for constructor param", e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            logger.error(logMarker, "Could not instantiate type for constructor param", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            logger.error(logMarker, "Could not instantiate type for constructor param", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isTypeString(Class klass) {
        return klass == String.class;
    }

    /**
     * Checks if a field´s type is primitve.
     *
     * NOTE: Strings are treated as primitve as well.
     * TODO Is the above mechanic good????
     *
     * @param klass The field, which´s type should be checked.
     *
     * @return True if the type of the passed field is primitive.
     *         False otherwise.
     */
    private boolean isTypePrimitive(Class klass) {
        return (klass == Integer.class  ||
                klass == Double.class   ||
                klass == Long.class     ||
                klass == Float.class    ||
                klass == String.class   ||
                klass.isPrimitive());
    }


    /**
     * Tries to find find a constructor for the passed class that matches a
     * specific parameter count.
     *
     * Throws a RuntimeException if either no constructor was found that meets the requirement of the number of
     * parameter or if there is more than one constructor that meets the number of parameters.
     *
     * @param klass         The class to find the constructor for.
     * @param paramCount    The number of parameters that the constructor must meet.
     *
     * @return An invokable constructor to construct an instance of the class.
     */
    private Constructor findConstructorForClass(Class klass, int paramCount) {
        Set<Constructor> constructors =  ReflectionUtils.getConstructors(klass, ReflectionUtils.withParametersCount(paramCount));
        if (constructors.size() == 0) {
            logger.error(logMarker, "Could not find appropriate constructor for class %s", klass.getClass().getName());
            throw new RuntimeException(String.format("Could not find appropriate constructor for class %s", klass.getClass().getName()));
        } else if (constructors.size() > 1) {
            logger.error(logMarker, "Found more than one possible constructor for class %s", klass.getClass().getName());
            throw new RuntimeException(String.format("Found more than one possible constructor for class %s", klass.getClass().getName()));
        }
        logger.info(logMarker, String.format("Found ctor with %d params for class %s", paramCount, klass.getName()));
        return (Constructor)constructors.toArray()[0];
    }

    /**
     * Looks for an implementation of an interface for the passed field.
     *
     * This method is intended to be used when the value for an interface is
     * omited in the configuration. In that case, the wizard checks if there
     * exists an implementation for that interface. In case it finds more than
     * one implementation, the wizard throws an Exception since it is unable to
     * decide which one to choose. The user has to specify it then.
     *
     * @param klass The field, into which the implementation should be injected.
     *              The underlying type must be an interface.
     *
     * @return      The class object of the found implementation.
     */
    private Class checkAndFindSingleImplementationOfInterface(Class klass) {
        // The config says that only one implementation of the interface exists.
        // Check it...
        List<Class> implementations = new ArrayList<>();
        implementations.addAll(reflections.getSubTypesOf(klass));
        if (implementations.size() > 1) {
            logger.error(logMarker, String.format("Can not inject into field %s. Found more than one implementation for type %s", klass.getName(), klass.getName()));
            throw new RuntimeException(String.format("Can not inject into field %s. Found more than one implementation for type %s", klass.getName(), klass.getName()));
        } else if (implementations.size() == 0) {
            logger.error(logMarker, String.format("Can not inject into field %s. Could not find implementation for type %s", klass.getName(), klass.getName()));
            throw new RuntimeException(String.format("Can not inject into field %s. Could not find implementation for type %s", klass.getName(), klass.getName()));
        }

        Class implementationClass = (Class)(implementations.get(0));
        logger.info(logMarker, String.format("Found implementation (%s) for interface %s", implementationClass.getName(), klass.getName()));

        return implementationClass;
    }
}
