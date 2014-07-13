unnecessary-wizard
==================

Groovy-Configurable DI-Container


Flexible Dependency-Injection that is configurable by groovy-code.

A simple Domain-Specific-Language (DSL) makes wiring up your application as simple as writing
a For-Loop to sum up a list of integers.

Want to taste it?

    injector {
      // The name of this injector
      name "ComplexInjection"

      // The type of this injector is a jerk
      type "Debug"

      // Here comes a list of injection targets
      // A injectionTarget is a class, which expects its dependencies to be injected

      injectionTarget {
        id "ComplexObject"
        className "de.tlongo.unnecessarywizard.java.test.objects.ComplexObject"

        fields {
            decimalValue new BigDecimal('22.3')
            list "java.util.ArrayList"
            sampleClass basePackage + "SampleClassToInject"
            singleInterface ""
        }
      }
    }

Take a closer look at this statement:

    decimalValue new BigDecimal('23.3')
    
The Wizard has just been born. The best is yet come!!!
