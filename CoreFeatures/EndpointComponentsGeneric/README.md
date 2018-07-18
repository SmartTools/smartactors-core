# Generic endpoint components feature

This feature contains implementations of endpoint components not dependent on endpoint implementation.

No dependencies on 3'rd party libraries allowed here unless components that use those libraries will wor with any implementation of endpoint.

## Endpoint configuration

Configuration sections common for all endpoint implementations are defined in this feature. Those sections are endpoint profiles section and endpoints section.

### Profiles section

Endpoint profile describes a set of pipelines that endpoint uses to process messages.

Configuration format:

```JavaScript
{
  "endpointProfiles": [
    {
      "id": ".. profile id ..",
      "pipelines": [
        {
          "id": ".. pipeline id ..",
          "stages": [
            {
              "type": ".. stage type ..",
              /* .. stage type specific parameters .. */
            },
            /* .. more stages .. */
          ]
        },
        /* .. other pipelines .. */
      ]
    },
    /* .. other profiles .. */
  ]
}
```

Each stage of pipeline represents a message handler (`IMessagehandler`) or sequence of zero one or more of handlers. The resulting pipeline is built of all the handlers described in all stages.

There is a special pipeline stage type that includes another pipeline:

```JavaScript
{
  "endpointProfiles": [
    {
      "id": "myProfile",
      "pipelines": [
        {
          "id": "pipeline1",
          "stages": [
            /* ... */
            {
              // All handlers of pipeline "pipeline2" will be
              // inserted instead of this stage
              "type": "include",
              "pipeline": "pipeline2"
            },
            /* ... */
          ]
        },
        {
          "id": "pipeline2",
          "stages": [
            /* ... */
          ]
        }
      ]
    }
  ]
}
```

*"include" stage looks for pipeline with required name in endpoint profile so pipeline may be overridden in another profile inheriting the profile with pipeline containing "include" stage.*

It's possible to refer to endpoint instance parameters from profile configuration:

```JavaScript
{
  "endpointProfiles": [
    {
      "id": "myProfile",
      "pipelines": [
        {
          "id": "pipeline1",
          "stages": [
            {
              "type": "error",
              
              // This refers to value of "myErrorMessage"
              // field of endpoint instance configuration
              // object. If there is no value then
              // "myErrorMessage" field of the same object
              // is used
              "message": "@@myErrorMessage",
              
              // This value will be used when no value defined
              // in endpoint instance configuration
              "myErrorMessage": "Default error message"
            }
          ]
        }
      ]
    }
  ]
}
```

See `EndpointProfilesConfigurationSectionStrategy` documentation for more details on profile inheritance order.

See `IEndpointProfile` and `IEndpointPipeline` documentation for more details on profile and pipeline configuration syntax.

### Endpoints section

Endpoint instances are created using `"endpoints"` configuration section. Endpoint instance is described by two main parameters - skeleton and profile. Endpoint skeleton is a implementation-specific code responsible for endpoint instance creation. Endpoint profile is a set of pipelines that endpoint uses to process messages.

Configuration format:

```JavaScript
{
  "endpoints": [
    {
      "skeleton": ".. skeleton name ..",
      "profile": ".. profile name ..",
      
      /** .. parameters required by skeleton and profile .. */
    },
    /* .. more endpoints .. */
  ]
}
```

## Message handlers

### Registering message handlers

Custom message handler type may be registered like this:

```java
IResolveDependencyStrategy myHandlerStrategy = /* ... */;

IAdditionDependencyStrategy storage = IOC.resolve(
        Keys.getOrAdd("expandable_strategy#endpoint message handler")
);

storage.register(
        "my handler type name",
        myHandlerStrategy
);
```

Handler resolution strategy will receive 4 parameters - type name, stage configuration, endpoint instance configuration and endpoint pipeline set. For convenience you may use `MessageHandlerResolutionStrategy` with lambda expression as parameter.

### Provided handler types

This feature provides some message handler types:

* `"default async unordered executor"`
    
    Executes consequent handlers asynchronously without order guarantees.
* `"create empty message"`
    
    Puts empty message object into inbound internal message environment.
* `"create environment"`

    Creates empty inbound internal message environment. Created environment contains message context ut **does not contain message object**.
* `"dead end"`

    Does nothing. Does not even forward message to consequent handlers.
    
    Useful when process reaches end of pipeline without sending any messages (default pipeline implementation throws when such situation happens).
* `"encoder/block/json"`

    Encodes outbound internal message as JSON document.
* `"decoder/block/json"`

    Decodes inbound external message as JSON document.
* `"exception interceptor"`

    Executes action when consequent handlers throw exception synchronously.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "exception interceptor",
      "action": ".. action dependency name ..",
      /* .. action-specific parameters .. */
    }
    ```

* `"exception forwarder"`
    
    Is alike to `"exception interceptor"` but sends a exception as source message to specified pipeline.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "exception forwarder",
      "pipeline": ".. pipeline name .."
    }
    ```

* `"fixed attribute router"`
    
    Chooses between configured handlers depending on value of some attribute of message.
    
    Configuration example:
    
    ```javaScript
    {
      "type": "fixed attribute router",
      "extractor": ".. attribute extractor name ..",
      "default": {
        /* .. handler configuration .. */
      },
      "routes": [
        {
          "value": ".. expected attribute value ..",
          /** .. handler configuration .. */
        }
      ]
    }
    ```
    
    `".. attribute extractor name .."` is a name of function extracting attribute value from message context
* `"global table attribute router"`

    Chooses a handler from global table to process a message depending on value of some attribute of message.
    
    Configuration example:
    
    ```javaScript
    {
      "type": "global table attribute router",
      "extractor": ".. attribute extractor name ..",
      "default": {
        /* .. handler configuration .. */
      },
      "table": ".. global table name .."
    }
    ```
    
    `".. attribute extractor name .."` is a name of function extracting attribute value from message context
    
    `".. global table name .."` is a name of handlers table
* `"response strategy setter"`
    
    Sets a response strategy.
    
    ```JavaScript
    {
      "type": "response strategy setter",
      
      // Name of pipeline transforming and sending response 
      "pipeline": ".. response pipeline name .."
    }
    ```
* `"default scope setter"`

    Executes consequent handlers in the same scope where endpoint instance was created.
* `"internal message sender"`

    Sends internal message.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "internal message sender",
      "stackDepth": 13,
      "chain": ".. initial message chain .."
    }
    ```
* `"error"`

    Unconditionally throws a exception. Makes sense when used with `"* attribute router"'s`.
    
    ```JavaScript
    {
      "type": "error",
      
      // Class of exception.
      // Optional, defaults to MessageHandlerException
      // Must inerit RuntimeException or MessageHandlerException
      // Musta have constructor accepting one String argument
      "errorClass": "java.lang.RuntimeException",
      
      // Exception message
      "message": ".. error message .."
    }
    ```
* `"add final actions"`

    Adds final actions to message processor of inbound internal message.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "add final actions",
      "actions": [
        {
          "action": ".. action name ..",
          ..
        },
        ...
      ]
    }
    ```

* `"outbound url parser"`
    
    Parses URL passed as part of internal outbound message. See JavaDoc for `info.smart_tools.smartactors.endpoint_components_generic.outbound_url_parser.OutboundURLParser` for more information.

* `"client callback/start"`

    Calls `#onStart()` method of a client callback and `#onError()` method of the same callack if error occurs synchronously in one of consequent handlers.

* `"client callback/success"`

    Calls `#onSuccess()` method of client callback.

* `"client callback/error"`

    Calls `#onError()` method of client callback. The source message must be a `Throwable`.

#### Actions for "exception interceptor"

Registering custom exceptional action:

```java
IAdditionDependencyStrategy storage = IOC.resolve(
        Keys.getOrAdd("expandable_strategy#exceptional endpoint action")
);
        
storage.register("action name", /* strategy */)
```

Strategy will take two parameters - action name and handler configuration object. Returned object should implement `IiAction<T, Throwable>` where `T` is connection context type.

##### Composite exceptional action

There is a action type that executes different action depending on exception type:

```JavaScript
{
  "type": "exception interceptor",
  "action": "composite",
  
  "exceptionClassActions": [
      {
          "class": ".. exception class ..",
          "action": ".. action name ..",
          /* .. action parameters .. */
      },
      /* .. more actions .. */
  ],
  "defaultAction": {
      "action": ".. action name ..",
      /* .. action parameters .. */
  }
}
```

#### Attribute extractors for "* attribute router"

Attribute extractor is a function (`IFunction`) that takes message context (`IMessageContext`) and returns some value.

Registering custom attribute extractor:

```java
IAdditionDependencyStrategy storage = IOC.resolve(
        Keys.getOrAdd("expandable_strategy#message attribute extractor")
);
        
storage.register("attribute name", /* strategy */)
```

Strategy will take extractor name and handler configuration object.

#### Handler tables for "global table attribute router"

Handler table is just a `Map` from attribute value to message handler. You may access such maps directly:

```java
Map<Object, IMessageHandler> table = IOC.resolve(
        Keys.getOrAdd("message handler table"),
        "table name"
);

table.put("some value", /* handler */)
```
Or create new tables:

```java
IAdditionDependencyStrategy storage = IOC.resolve(
    Keys.getOrAdd("expandable_strategy#message handler table")
);

storage.register("table name", new SingletonStrategy(new HashMap()))
```

There are few tables provided by this feature:

* `"default block decoders by mime type"`
* `"default block encoders by mime type"`
* `"default stream decoders by mime type"`
* `"default stream encoders by mime type"`

These tables store message encoders and decoders by MIME type. Modifying them you may add support of new mime types to all endpoints that use these tables.

## Endpoint skeletons

There is no interface for endpoint instance or endpoint skeleton.

```java
IAdditionDependencyStrategy skeletonStorage = IOC.resolve(
    Keys.getOrAdd("expandable_strategy#create endpoint")
);

skeletonStorage.register(
        "mySkeletonName",
        /* strategy */
);
```

Strategy will take three parameters - skeleton name, endpoint instance configuration and pipeline set (`IEndpointPipelineSet`). 
It may return object of any type or even `null`.
