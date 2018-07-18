# Endpoint interfaces feature

This feature contains implementation-independent interfaces of endpoint components.

## Common terms

* External message - a object representing a message transferred outside of messaging system. That may be a object representing HTTP request/response, UDP datagram, etc. Classes of objects representing external messages depend on endpoint implementation.

* Internal message - a message, processed inside of messaging system. In SmartActors the internal message is a `IObject` containing serializable data associated with few other objects (including message processor and context). In terms of endpoints subsystem internal message is a `IObject` containing the message and all object associated with it (such object is also called "message environment").

* Inbound message - message received from external system. "Inbound message" may refer as to external message representing received message (inbound external message) as to internal message such message is converted to (inbound internal message).

* Outbound message - message sent to external system. "Outbound message" may refer as to internal message representing a request to send a message to external system (internal outbound message) as to external message sent or being prepared to be sent to external system (external outbound message).

* Message transformation - transformation of one message (called **source message**) to another (called **destination message**). Possible transformation directions are "external inbound message to internal inbound message" and "internal outbound message to external outbound message". Some operations are common for both directions and may be implemented once for both cases that's why terms "source message" and "destination message" are used where possible.

## Message transformation

Message transformation is the only kind of functionality common for all types of endpoints so most interfaces in this feature are related to message transformation.

Single step of message transformation is represented by object implementing `IMessageHandler` interface. Message handler has only one method that takes a message context and callback that will delegate transformation of message to the next message handlers. Use of callbacks allows message handlers to execute next handlers asynchronously, aggregate, filter or split messages, process exceptions thrown by consequent handlers, etc.

`IMessageContext` (do not confuse with context `IObject` associated with internal messages) contains objects associated with message transformation process. Default subtype (`IDefaultMessageContext`) stores the following objects: source message, destination message and connection context. `IMessageContext` provides a `#cast(Class<? extends IMessageContext>)` method that should be used every time the context is converted to type with different erasure type; context implementation may generate new class implementing required interface when `cast()` method is called.

Endpoint pipeline (`IEndpointPipeline`) is a sequence of message handlers that represents a whole transformation process or some part of such process. `IEndpointPipelineSet` is a collection of pipelines used by one endpoint instance that lazily creates required pipelines from endpoint configuration and some endpoint profile (`IEndpointProfile`).

One of common operations with external messages is access to message body represented as array of bytes. For such operations message may be wrapped into objects implementing `IInboundMessageByteArray` or `IOutboundMessageByteArray` depending on message direction (inbound or outbound).

## Outbound channels

Some endpoints need to provide a ability to send messages by request from inside of messaging system. or such purposes there is a `IOutboundConnectionChannel` interface that provides a method for sending a outbound message. There must exist a global storage of outbound channels that allows endpoints to register and unregister outbound channels and allows to access a channel by it's identifier.
