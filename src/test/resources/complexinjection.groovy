/**
 * Created by tolo on 05.05.2014.
 */

basePackage = "de.tlongo.unnecessarywizard.java.test.objects."

injector {
    // The name of this injector
    name "ComplexInjection"

    // The type of this injector
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        targetName basePackage+"ComplexObject"
        decimalValue new BigDecimal('22.3')
        list "java.util.ArrayList"
        sampleClass basePackage+"SampleClassToInject"
        singleInterface ""
    }
}