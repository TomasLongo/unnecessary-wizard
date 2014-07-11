import de.tlong.unnecessarywizard.groovy.InjectionMethod

/**
 * Created by tolo on 15.04.2014.
 */

import de.tlong.unnecessarywizard.groovy.InjectionMethod

injector {
    // The name of this injector
    name "SuperInjector"

    // The type of this injector
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    //This should cause an error
    injectionTarget {
        id "MyTarget"

        injectionMethod InjectionMethod.CONSTRUCTOR
        fields {
            fieldNameOne "classToInject"
        }
    }
}