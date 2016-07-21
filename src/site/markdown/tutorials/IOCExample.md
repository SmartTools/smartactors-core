# How to use IOC

## Overview

[IoC](https://en.wikipedia.org/wiki/Inversion_of_control) means Inversion of Control.
It's a common design principle, and SmartActors, as many other popular frameworks, has it's own IoC container.

The access to IoC is done through static [service locator](https://en.wikipedia.org/wiki/Service_locator_pattern) called [`IOC`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/ioc/IOC.html)
The main methods are `void IOC.register(IKey key, IResolveDependencyStrategy strategy)` and `T IOC.resolve(IKey<T> key, Object... args)`.
The container resolves objects by the [`IKey`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/ikey/IKey.html).
The object resolving is delegated to a [`IResolveDependencyStrategy`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/iresolve_dependency_strategy/IResolveDependencyStrategy.html).
There are a couple of predefined strategies.

## Key

First of all, you need a key to register your strategy and resolve objects.
The simplest way is to use the `new` operator.

    IKey newKey = new Key("sample");
    
However, the recommended way to get the key is to resolve it with IOC.

    IKey resolveKey = IOC.resolve(IOC.getKeyForKeyStorage(), "sample");

`IOC.getKeyForKeyStorage()` produces the key to get the key.
    
This magic is hidden in `Keys` class, so it's necessary just to call `Keys.getOrAdd()`.

    IKey key = Keys.getOrAdd("sample");
    
## Initialization

To resolve the key, the corresponding strategy should be registered before.
Also, the default IoC implementation requires the [`Scope`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/iscope/IScope.html) to be initialized.
So this initialization code is required (usually it's already called by the server implementation):

    Object scopeKey = ScopeProvider.createScope(null);
    IScope scope = ScopeProvider.getScope(scopeKey);
    ScopeProvider.setCurrentScope(scope);
    scope.setValue(IOC.getIocKey(), new StrategyContainer());
    IOC.register(IOC.getKeyForKeyStorage(), new ResolveByNameIocStrategy(
            (a) -> {
                try {
                    return new Key((String) a[0]);
                } catch (InvalidArgumentException e) {
                    throw new RuntimeException(e);
                }
            })
    );

The [`ResolveByNameIocStrategy`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/resolve_by_name_ioc_with_lambda_strategy/ResolveByNameIocStrategy.html) it responsive to create Keys by the name as it was demonstrated above.

## Singleton strategy

When you have a key, you can register the resolving strategy.
For example, [`SingletonStrategy`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/singleton_strategy/SingletonStrategy.html):

    IKey key = Keys.getOrAdd("singleton");
    SampleClass sampleObject = new SampleClass("singleton");
    IOC.register(key, new SingletonStrategy(sampleObject));
    
This strategy always returns the same object instance, given to it's constructor.

    SampleClass resolveObject1 = IOC.resolve(key);
    SampleClass resolveObject2 = IOC.resolve(key);
    
Both these variables point to the same object.

## New instance strategy

The [`CreateNewInstanceStrategy`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/create_new_instance_strategy/CreateNewInstanceStrategy.html) creates a new object for each call to `resolve()`.
You should define a [lambda expression](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) to create your objects and pass it to the strategy constructor.
 
    IKey key = Keys.getOrAdd("new");
    IOC.register(key, new CreateNewInstanceStrategy(
            (args) -> new SampleClass((String) args[0])));
            
Then you can resolve instances of your class.

    SampleClass resolveObject1 = IOC.resolve(key, "id1");
    SampleClass resolveObject2 = IOC.resolve(key, "id1");
    SampleClass resolveObject3 = IOC.resolve(key, "id3");
    
All returned objects are different objects. 
However, these objects can be equal (but not the same) if you pass the same parameters to the `resolve()` method (string id in this example).
