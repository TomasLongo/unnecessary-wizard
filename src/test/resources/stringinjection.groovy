/**
 * Created by tolo on 26.04.2014.
 */

injector {
    // The name of this injector
    name "SimpleStringInkection"

    // The type of this injector
    type "Debug"

    // The packages in which the classes are meant to be found
    packagesToScan << "package.A" << "package.B"

    // Here comes a list of injection targets
    // A injectionTarget is a class, which expects its dependencies to be injected
    injectionTarget {
        targetName "de.tlongo.unnecessarywizard.java.test.objects.SimpleStringInjection"
        fieldToInject "This string was injected"
        secondField "If this works..."
        thirdField "I go nuts"
    }

}