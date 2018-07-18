# Netty endpoint components feature

This feature contains implementations of components for netty-based endpoints.

## Transports

Netty provides different implementations of channels and event loops called transports. The default transport is based on NIO. There are also available some platform-specific native transport implementations for Linux (uses `epoll`) and Mac/BSD (uses KQueue). There also is a OIO (old IO) locking transport that is not as efficient but still required in some situations.

This feature provides a abstraction over netty transports - interface `INettyTransportProvider` instances of which create objects for specific transport implementation.

The following transport types are registered by default plugin:

* `"nio"` - use NIO transport on any platform
* `"prefer-native"` - use native transport if possible, fallback to NIO
* `"force-native"` - use native transport if possible, fail otherwise
* `"blocking"` - use OIO transport

## Event loops configuration section

This feature adds a configuration section named `"nettyEventLoops"` that may be used to configure event loops used by Netty endpoints to poll channels.

Example of event loop configuration for a server with two (HTTP and HTTPS) server endpoints:

```javascript
{
    "nettyEventLoops": [
        {
            "id": "defaultServerParent",        // Loop group name
            "upcounter": "root",                // Upcounter name
            "transport": "prefer-native",       // Transport type
            "threads": 1                        // Transport-specific parameters
        },
        {
            "id": "defaultServerChild",
            "upcounter": "root",
            "transport": "prefer-native",
            "threads": 4
        },
        
        {
            "id": "httpServerParent",           // Loop group alias
            "alias": "defaultServerParent"
        },
        {
            "id": "httpServerChild",
            "alias": "defaultServerChild"
        },
        
        {
            "id": "httpsServerParent",
            "alias": "defaultServerParent"
        },
        {
            "id": "httpsServerChild",
            "alias": "defaultServerChild"
        }
    ]
}
```

Aliases are defined to make it possible to change loop groups used by endpoint without editing endpoint configuration. This is necessary to provide ability to tune endpoint performance independently from endpoint logic and (probably) define different configurations for different server instances.

Event loop groups described in `"nettyEventLoops"` section are created lazily (group is created when a first endpoint requiring a group is created). Until the group is created the group configuration may be overwritten by another configuration (such behaviour makes it possible to define some default group configuration in feature containing endpoint type definition and override it in some other feature that contains a endpoint of defined type or in a feature that loads before any feature containing endpoint of defined type). Configuration trying to overwrite configuration of already created group will cause exception.

## Message handlers

This feature registers some message handlers specific for Netty-based endpoints. Here are some of them:
    
* `"netty/retain message"`
    Retains (increases reference counter of) Netty message. If the next handlers throw synchronously then this handler releases the message.
    
    Configuration example:
        
    ```JavaScript
    {
      "type": "netty/retain message",
      "messageExtractor": "message extractor name"
    } 
    ```
    
    Where `"message extractor name"` is one of:
    * `"unwrapped inbound"` - raw inbound message
    * `"wrapped inbound"` - inbound message wrapped into message byte array
    * `"unwrapped outbound"` - raw outbound message
    * `"wrapped outbound"` - outbound message wrapped into message byte array
    
* `"netty/release message"`
    Releases (decreases reference counter of) Netty message after next handlers exit synchronously with or without exception.
    
    Configuration example:
        
    ```JavaScript
    {
      "type": "netty/release message",
      "messageExtractor": "message extractor name"
    } 
    ```
    
    For meaning of `"message extractor name"` see previous item.
    
* `"netty/send outbound message"`
    Sends outbound Netty message. Does not call next handlers.
    
    Configuration example:
        
    ```JavaScript
    {
      "type": "netty/send outbound message"
    }
    ```
    
* `"netty/wrap inbound message"`
    Wraps inbound message into message byte array. Message should implement `ByteBufHolder`
    
    ```JavaScript
    {
      "type": "netty/wrap inbound message"
    }
    ```
    
* `"netty/setup/inbound chanel handler"`
    Set up handler in Netty channel pipeline that will notify some pipelines on inbound messages and errors.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/setup/inbound chanel handler",

      "pipeline": ".. pipeline name ..",

      "errorPipeline": ".. pipeline name ..",
    
      // Only messages of classes inheriting given class will be routed to the pipeline
      "messageClass": ".. canonical name of message class .."
    }
    ```
    
* `"netty/setup/attach dynamic outbound channel"`
    Creates a outbound channel associated with the Netty channel.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/setup/attach dynamic outbound channel",
    
      // Pipeline to use to convert internal outbound message to external message
      "pipeline": ".. pipeline id ..",
      
      // Listener that should be notified on created channel lifecycle.
      // Use "global outbound connection channel storage channel listener" to make channel available globally
      "listener": ".. channel listener dependency name .."
    }
    ```
* `"netty/store outbound channel id"`
    Stores id of outbound channel associated with the Netty channel in internal inbound message.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/store outbound channel id"
    }
    ```
    
    Resulting environment structure:
    
    ```JavaScript
    {
      "context": {
        "channelId": ".. identifier of the channel .."
      }
    }
    ```

* `"netty/http cookie setter"`
    Sets cookies to outbound HTTP response.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/http cookie setter"
    }
    ```
    
    Required outbound environment structure:
    
    ```JavaScript
    {
      "context": {
        "cookies": [
          {
            "name": ".. cookie name ..",
            "value": ".. value ..",
            "maxAge": ".. age ..",
            "path": ".. path .."
          },
          // ... more cookies ...
        ]
      }
    }
    ```
* `"netty/http headers setter"`
    Sets headers to outbound HTTP message (request or response).

    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/http headers setter"
    }
    ```
    
    Required outbound environment structure:
    
    ```JavaScript
    {
      "context": {
        "headers": [
          {
            "name": ".. header name ..",
            "value": ".. value .."
          },
          // ... more headers ...
        ]
      }
    }
    ```

* `"netty/http response status code setter"`
    Sets status code of outbound HTTP response.
    
    Configuration example:

    ```JavaScript
    {
      "type": "netty/http response status code setter"
    }
    ```
    
    Required outbound environment structure:
    
    ```JavaScript
    {
      "context": {
        "httpResponseStatusCode": 418
      }
    }
    ```
    
    (where 418 is the code you want to return). If no code provided response code is not changed (default is 200).

* `"netty/fixed http path parser"`
    Parses path of inbound HTTP request.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/fixed http path parser",
      "templates": [/* .. temlplate list ..*/]
    }
    ```
    
    See `ParseTree` documentation for template syntax.
    
* `"netty/http query string parser"`
    Parses inbound HTTP request's query string.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/http query string parser",
      "templates": [/* .. temlplate list ..*/]
    }
    ```
    
* `"netty/http response metadata presetup"`
    Prepares context of inbound internal message created from inbound HTTP request.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/http response metadata presetup"
    }
    ```
    
* `"netty/setup/http server channel"`
    Sets up HTTP codec and aggregator on server HTTP channel.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/setup/http server channel",
      "maxAggregatedContentLength": 1024
    }
    ```
    
* `"netty/setup/http client channel"`
    Sets up HTTP codec and aggregator on client THHP channel.

    Configuration example:

    ```JavaScript
    {
      "type": "netty/setup/http client channel",
      "maxAggregatedContentLength": 1024
    }
    ```
    
* `"netty/setup/http web-socket upgrade listener"`
    Sets up Web-socket upgrade listener on server HTTP channel.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/setup/http web-socket upgrade listener",
    
      // pipeline that will be notified when channel upgrade to Web-socket occurs
      "pipeline": ".. pipeline name ..",
    
      // Web-socket path. Only one path per listener is supported
      "path": ".. web-socket path .."
    }
    ```
    
* `"netty/ssl-setup/server"`
    Enables SSL for TCP server channel.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/ssl-setup/server",
      "ciphers": [/* .. list of names of supported ciphers .. */]
    }
    ```
    
    Endpoint configuration example:
    
    ```JavaScript
    {
      /* .. other parameters .. */
    
      // Certificate path
      "serverCertificate": "/etc/ssl/server.crt",
    
      // Private key file path. File must have PKCS#8 format.
      "serverCertificateKey": "/etc/ssl/server.key",
    
      // Private key password
      "serverCertificateKeyPassword": "password"
    }
    ```
    
* `"netty/ssl-setup/client"`
    Enables SSL for TCP client channel.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/ssl-setup/client",
      "ciphers": [/* .. list of names of supported ciphers .. */]
    }
    ```

* `"netty/client/acquire channel"`
    Creates a pool of client connections (when created) and retains a channel from that pool and passes it as connection context to next handler when receives a message.
    
    Configuration example:
    
    ```JavaScript
    {
      "type": "netty/client/acquire channel",
      
      // This pipeline will be notified when a new
      // channel is actually created.
      // It must setup Netty pipeline.
      "setupPipeline": ".. name of setup pipeline ..",
      
      // Use default pool implementation.
      // It uses Neety's built-in connection pool.
      // Alternative is a "none" type that creates
      // a new connection every time.
      "poolType": "default",
      
      // Default pool parameters:
      "transport": ".. transport kind ..",
      "eventLoopGroup": ".. event loop group name ..",
      
      // Timeout in milliseconds.
      // Optional, by default no timeout applied.
      "readTimeout": 5000,
    }
    ```

* `"netty/client/release channel"`
    Releases channel acquired fromm pool by `"netty/client/acquire channel"` handler.

* `"netty/client/bind request to channel"`
    Stores a source message in attribute of a channel.

* `"netty/client/get bound request"`
    Reads a message stored in channel attribute by `"netty/client/bind request to channel"` and stores it in `"request"` field of destination environment.
