import de.tlongo.unneccesarywizard.java.core.Configuration

/**
 * Created by Tomas Longo on 15.04.2014.
 */

injector {
    // The name of this injector
    name "SuperInjector"

    // The type of this injector
    type "Debug"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected

    injectionTarget {
        id "MyClassA"
        className "my.package.ClassA"
        fields {
            fieldNameOne "classToInject"
        }
    }

    injectionTarget {
        id "MyClassB"
        className "my.package.ClassC"
        fields {
            fieldNameOne "classToInject"
        }
    }

    injectionTarget {
        id "MyClassC"
        className "my.package.ClassC"
        fields {
            fieldNameOne "classToInject"
        }
    }

    injectionTarget {
        id "ConstructorInjection"
        className "my.package"
        injectionMethod Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR

        constructorParams {
            param1 "stringParam"
            param2 23.00f
        }
    }

    injectionTarget {
        id "MultiValueInjectionInfo"
        className "my.pacakge"

        fields {
            fieldOne (value:"de.tlongo.MyClass", scope:Configuration.InjectionTarget.Scope.SINGLETON)
        }
    }
}