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
        id "SingletonHolder"
        className "de.tlongo.unnecessarywizard.java.test.objects.SingletonHolder"

        fields {
            singleton(value:"de.tlongo.unnecessarywizard.java.test.objects.Singleton", scope: Configuration.InjectionTarget.Scope.SINGLETON)
        }
    }

    injectionTarget {
        id "anotherholder"
        className "de.tlongo.unnecessarywizard.java.test.objects.SingletonHolder"

        fields {
            singleton (value:"de.tlongo.unnecessarywizard.java.test.objects.Singleton", scope:Configuration.InjectionTarget.Scope.SINGLETON)
        }
    }

    injectionTarget {
        id "constructor"
        className "de.tlongo.unnecessarywizard.java.test.objects.SingletonHolder"

        injectionMethod Configuration.InjectionTarget.InjectionMethod.CONSTRUCTOR

        constructorParams {
            param1 (value:"de.tlongo.unnecessarywizard.java.test.objects.Singleton", scope:Configuration.InjectionTarget.Scope.SINGLETON)
        }
    }


}