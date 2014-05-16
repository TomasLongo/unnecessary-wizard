/**
 * Created by tolo on 15.04.2014.
 */

injector {
    // The name of this injector
    name "SuperInjector"

    // The type of this injector
    type "Debug"

    // The packages in which the classes are meant to be found
    packagesToScan << "package.A" << "package.B"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        targetName "ClassA"
        fields {
            fieldNameOne "classToInject"
        }
    }

    injectionTarget {
        targetName "ClassB"
        fields {
            fieldNameOne "classToInject"
        }
    }

    injectionTarget {
        targetName "ClassC"
        fields {
            fieldNameOne "classToInject"
        }
    }
}