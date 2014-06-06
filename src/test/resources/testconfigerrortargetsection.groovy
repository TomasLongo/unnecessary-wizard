/**
 * Created by tolo on 15.04.2014.
 */

injector {
    // The name of this injector
    name "SuperInjector"

    // The type of this injector
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    //This should cause an error
    injectionTarget {
        targetName "ClassA"

        balalkhf "lajsdf"
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