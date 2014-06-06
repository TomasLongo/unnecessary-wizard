/**
 * Created by tolo on 05.05.2014.
 */

injector {
    // The name of this injector
    name "SimplePrimitiveInjection"

    // The type of this injector
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget{
        id "Simple"
        className "de.tlongo.unnecessarywizard.java.test.objects.SimplePrimitiveInjection"
        fields {
            intField 123
            longField 456L
            floatField 23.44f
            doubleField 0.456789D
        }
    }
}