# SimpleActors

Basic actor library intended for usage in CLI tools for SmartActors framework.

## Usage
### Actors
Create actors that will be used in routing slips. They must implement either of the following abstract classes:

* `StatelessActor` - the most basic actor, can be used in most cases
* `StatefulActor` - actor with synchronized methods, can be used for multithreaded slips

The most basic actor may look like something like this:

```java
import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.util.Locale;

public class StringToUpperCaseActor extends StatelessActor {

    @Executable
    public String upperCaseString(String string) {
        return string.toUpperCase(Locale.ROOT);
    }
}
```

Note the `@Executable` annotation. When method has this annotation, then it'll be available for usage in routing slips.

#### Registering actors
Actor can be registered in SimpleActors during initialization.

```java
import info.smart_tools.simpleactors.SimpleActorsStarter;

public class Application {

    public static void main(String[] args) {
        SimpleActorsStarter starter = new SimpleActorsStarter()
                .addActor("upperCaseStringActor", new StringToUpperCaseActor())
                .addActor("stringGetterActor", new StringGetterActor());
        
        // convert args to IArguments object
        // you can use whatever arguments parsing library you have or write your own
        // IArguments arguments = new Arguments(args);
        starter.start(arguments);
    }
}
```

### Routing slips
Routing slips are located in `src/resrouces/{tool_name}_routing_slips` folder. They have the following structure:

```json
{
  "name": "make string upper case",
  "steps": [
    {
      "actorName": "stringGetterActor",
      "methodName": "returnString",
      "methodParameters": {
        "argumentPaths": [],
        "responsePath": "acquired string"
      }
    },
    {
      "actorName": "upperCaseStringActor",
      "methodName": "upperCaseString",
      "methodParameters": {
        "argumentPaths": ["acquired string"],
        "responsePath": "upper case string"
      }
    }
  ]
}
```

`{tool_name}` variable is passed to `SimpleActorsStarter.start()` as an argument.

#### Slip step structure
* `actorName` - name of the actor. Note that the name used here is the same name that's passed as key value in `addActor()` method
* `methodName` - name of the method in the actor. 
* `argumentPaths` - paths for values to be sent to actor's method
* `responsePath` - where to put returning value

#### Available parameters for paths
Aside from simply passing value from one method to another, additional operations can be performed on values. 
Here's the list of all possible modifications for paths:

* `#slip` - reference to the current slip
* `#slip_storage` - reference to map with all registered slips
* `#message` - reference to the message as a whole
* `#split response` - if the returning value from method is an instance of `Map`, then put all values to the message with matching keys
* `#constant` - used to store constant value. Right now there are four types of constants:
    * `#constant#string:` - string value
    * `#constant#int:` - integer value
    * `#constant#double:` - double value
    * `#constant#boolean:` - boolean value

### Calling routing slips
To call routing slip, you need to return its name as a return value of `IArguments.getCommand()`. Internally, it'll be called this way:

```java
IRoutingSlip slip = slips.get(arguments.getCommand())
if (slip != null) {
    // execute slip
} else {
    throw new ProcessExecutionException("Routing slip not found")
}
```
