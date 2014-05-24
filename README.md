unnecessary-wizard
==================

Groovy-Configurable DI-Container


Flexible Dependency-Injection that is configurable by groovy-code.

A simple Domain-Specific-Language (DSL) makes wiring up your application as simple as writing
a For-Loop to sum up a list of integers.

Want to taste it?

    injector {
      // The name of this injection configuration
      name "Injection"
  
      // The type of this injector
      type "Debug"
  
      // The packages in which the classes are meant to be found
      packagesToScan << "de.tlongo.unnecessarywizard.java.test.objects"
  
      // Here comes a list of injection targets
      // A injectionTarget is a class, which expects its dependencies to be injected
  
      injectionTarget {
          targetName "de.tlongo.unnecessarywizard.java.test.objects.ComplexObject"
          decimalValue new BigDecimal('22.3')
          list "java.util.ArrayList"
          sampleClass "SampleClassToInject"
          singleInterface ""
      }
    }

Take a closer look at line 29!!

The Wizard has just been born. The best is yet come!!!
