/**
 * Created by tolo on 05.05.2014.
 */

import de.tlong.unnecessarywizard.groovy.InjectionMethod;

basePackage = "de.tlongo.unnecessarywizard.java.test.objects."

injector {
    // The name of this injector
    name "ConstructorInjection"

    // The type of this injector is a jerk
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        id "ComplexObject"
        className basePackage+"ComplexObject"

        injectionMethod InjectionMethod.CONSTRUCTOR

        fields {
            decimalValue new BigDecimal('22.3')
            list "java.util.ArrayList"
            sampleClass basePackage + "SampleClassToInject"
            singleInterface ""
        }
    }
}