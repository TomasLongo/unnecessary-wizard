/**
 * Created by tolo on 05.05.2014.
 */

injector {
    // The name of this injector
    name "SimplePrimitiveInjection"

    // The type of this injector
    type "Debug"

    // The packages in which the classes are meant to be found
    packagesToScan << "package.A" << "package.B"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget("de.tlongo.unnecessarywizard.java.test.objects.ComplexObject",
                    [decimalValue:new BigDecimal('22.3'),
                     list:"java.util.ArrayList"])
}