package de.tlongo.unneccesarywizard.java.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by tolo on 08.07.2014.
 */
public class ConstructorInjector implements InjectionMethod {
    Logger logger = LoggerFactory.getLogger(ConstructorInjector.class);

    ClassInstantiator instantiator = new DefaultInstantiator();

    private Reflections reflections = new Reflections("de.tlongo.unnecessarywizard");

    @Override
    public Object performInjection(Configuration.InjectionTarget target) {
        logger.debug(String.format("Using constructor injection for target %s", target.getId()));

        Class targetClass;
        try {
            targetClass = Class.forName(target.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Constructor ctor = findConstructorForClass(targetClass, target.getConstructorParams().size());

        final Class[] ctorParamTypes = ctor.getParameterTypes();
        final List<Object> evaluatedParams = new ArrayList<>();

        IntStream.range(0, ctorParamTypes.length).forEach(index -> {
            Object ctorParam = target.getConstructorParams().get(index);
            Class klass = ctorParamTypes[index];
            if (isTypePrimitive(klass) || isTypePrimitive(klass)) {
                evaluatedParams.add(ctorParam);
            } else if(klass.isInterface()) {
                // We have to inject an interface here
                Class interfaceImpl;
                if (StringUtils.isEmpty((String)ctorParam)) {
                    // The config says, that there is only one implementation.
                    // Check and find it...
                    interfaceImpl = checkAndFindSingleImplementationOfInterface(klass);
                    evaluatedParams.add(instantiator.instantiate(interfaceImpl));
                } else {
                    // The implementation was provided in the config
                    evaluatedParams.add(instantiator.instantiate((String)ctorParam));
                }
            }
        });

        logger.debug(String.format("Trying to find a ctor with %d params for class %s", target.getConstructorParams().size(), targetClass.getName()));

        try {
            return ctor.newInstance(evaluatedParams.toArray());
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        //return targetObject;
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

    Constructor findConstructorForClass(Class klass, int paramCount) {
        Set<Constructor> constructors =  ReflectionUtils.getConstructors(klass, ReflectionUtils.withParametersCount(paramCount));
        if (constructors.size() == 0) {
            throw new RuntimeException(String.format("Could not find appropriate constructor for class %s", klass.getClass().getName()));
        } else if (constructors.size() > 1) {
            throw new RuntimeException(String.format("Found more than one possible constructor for class %s", klass.getClass().getName()));
        }
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
            throw new RuntimeException(String.format("Can not inject into field %s. Found more than one implementation for type %s", klass.getName(), klass.getName()));
        }        if (implementations.size() == 0) {
            throw new RuntimeException(String.format("Can not inject into field %s. Could not find implementation for type %s", klass.getName(), klass.getName()));
        }

        return (Class)(implementations.get(0));
    }
}
