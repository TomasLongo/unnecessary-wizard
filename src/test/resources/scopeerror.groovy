import de.tlongo.unneccesarywizard.java.core.Configuration
import de.tlongo.unneccesarywizard.java.core.InjectionMethod

/**
 * Created by Tomas Longo on 15.04.2014.
 *
 * This script contains an error.
 * The object 'de.tlongo.unnecessarywizard.java.test.objects.Singleton' is injected into 2 targets with different scopes
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
            singleton "de.tlongo.unnecessarywizard.java.test.objects.Singleton"
        }
    }
}