# How to write Actor

## Overview

Actor in SmartActors is a piece of code which should be written by a developer. Actually it's not an actor in terms of [actor model](https://en.wikipedia.org/wiki/Actor_model), but a set of handlers run inside general actors in the system.

Actors are built into a [message map](MessageMapExample.html). The same instance of the actor can be used in multiple places of multiple maps. Or different instances can be created.

Anyway the actor implementation should not be thread-safe in terms of Java. It's guaranteed that during the actor's handler call, no other thread may access the message passed to the handler. Such isolation is made per actor instance, so calls to any handlers of the same actor are always sequential.

The actors handler is just a method. The message to process is passed to the handler as an object of some interface, defined by the method signature. Getters of the interface retrieves data from the message. Setters of the interface puts data to the message. We call it [wrapper](WrapperExample.html).

## Actor requirements

It's not required for Actor to implement some specific interface or extend some specific class.

### Constructor

The creation of your actor in the system is controlled by [IOC](IOCExample.html) strategy. You are free to create any strategy for your needs. The creation of the actor instance is in your hands. You should pack your actor's code and the initialization strategy into a [Plugin](PluginExample.html).

In simplest way you can use the default constructor (without arguments).

If your actor requires configuration, you can pass [IObject](IObjectExample.html) with configuration parameters to it's constructor.

### Handlers

Handler is a method of your actor.

This method must return `void`.

The method must accept only one parameter of the specified interface. It can be any interface, and typically you declare this interface together with the actor. It's recommended for the interface to be simplest, to contain getters and setters only for data required in the current handler, no more. The system puts a wrapper over the currently processing message, implementing the interface, as the parameter when the handler is called.

The method may throw any exception, even no exceptions is acceptable. However, it's recommended to define a special type of exception for your actor and wrap any lower level exceptions with it.
 
So, the 'hello world' handler may look like this:
 
    public void hello(final GreetingMessage message) throws HelloActorException {
        try {
            String name = message.getName();
            message.setGreeting(String.format("Hello, %s!", name));
        } catch (ReadValueException | ChangeValueException e) {
            throw new HelloActorException(e);
        }
    }
    
Note you can have multiple handlers in the same actor. It may be required when you have to keep a state between multiple points of the message map: you put handlers of the same actor to the map and keep the state in the actor fields. It's guaranteed by the actor system the handlers will be called sequentially. However, the order of calls is undefined: it's only known that during processing of the same message through the map the first handler in the map will be called before the second, etc., but these calls may be interleaved by another messages.
    
### Wrappers

The parameter for the handler is the interface. It should have getters and setters.

Getter is to retrieve a value from the processing message. Like usual getter it returns some value and takes no parameters. Additionally it must throw [`ReadValueException`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/iobject/exception/ReadValueException.html).

Setter is to put the value to the processing message. Like usual setter it returns `void` and takes one parameter — the value. Additionally it must throw [`ChangeValueException`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/iobject/exception/ChangeValueException.html).

So, the interface for the 'hello world' handler should be this:

    public interface GreetingMessage {
        String getName() throws ReadValueException;
        void setGreeting(String greeting) throws ChangeValueException;
    }
    
Note the source of the data for getters and the target of the data for setters is defined externally to the actor. It's the message map configuration. The data may come not only from the current message, but also can be put to the future answer or be taken from the processing context. See details in [wrapper tutorial](WrapperExample.html).
    
## Test actor

You don't need any IoC, actor system, server or other special environment to test your actor. You need mocks like [Mockito](http://mockito.org/).

Just mock the message passed to the handler.

    GreetingMessage message = mock(GreetingMessage.class);
    when(message.getName()).thenReturn("Test");

Call the handler.

    actor.hello(message);
    
And check that the correct methods of the message where called back.

    verify(message).setGreeting(eq("Hello, Test!"));
    
In the same way you can check the behavior of your actor if it's not possible to get or set value in the message.

    when(message.getName()).thenThrow(ReadValueException.class);

## Packaging

It's a good practice to create your actors absolutely independent. They don't depend on SmartActors infrastructure, only message interface methods must throw specific exceptions. Test your actors with mocks only. Keep your actors in a separate Maven module, package it into a separate JAR file.

Then you can provide a plugin to allow to resolve your actors with IoC. The plugin, again, should be a separate module.

When all your actors will be in separate modules it'll be easy to join them together to message maps and, finally, to your complex application.

## Code

The sources of this tutorial:

* [HelloActor implementation](http://smarttools.github.io/smartactors-core/xref/info/smart_tools/smartactors/core/examples/actor/package-frame.html)
* [Tests](http://smarttools.github.io/smartactors-core/core.examples/xref-test/info/smart_tools/smartactors/core/examples/ActorExample.html)
