import de.tlongo.unneccesarywizard.java.core.Configuration

/**
 * Created by tolo on 05.05.2014.
 */

basePackage = "de.tlongo.unnecessarywizard.java.test.objects"

injector {
    // The name of this injector
    name "ConstructorInjection"

    // The type of this injector is a jerk
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        id "CtorInjection"
        className basePackage+".CtorInjection"

        injectionMethod Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR

        constructorParams {
            param1 "string"
            param2 ""
            param3 23.00f
            param4 23.00D
            param5 23
            param6 23L
            param7 new BigDecimal('23.00')
            param8 "${basePackage}.SampleClassToInject"
        }
    }
}