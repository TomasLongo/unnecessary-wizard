/**
 * Created by tolo on 05.05.2014.
 */

injector {
    // The name of this injector
    name "ComplexInjection"

    // The type of this injector
    type "Debug"

    // The packages in which the classes are meant to be found
    packagesToScan << "de.tlongo.unnecessarywizard.java.test.objects"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        id "Failed"
        className "de.tlongo.unnecessarywizard.java.test.objects.ComplexObject"

        // This injection must throw an Exception since there is more than one
        // implementatin for this interface and the wizard can not tell which
        // one to use
        testInterface ""
    }
}