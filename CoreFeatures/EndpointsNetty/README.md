# Netty endpoint skeletons feature

This feature contains skeletons of Netty-based endpoints.

The following skeletons are provided:

* single-port TCP server (`"netty/server/tcp/single-port"`)
    
    Configuration example:
    
    ```JavaScript
    {
      "skeleton": "netty/server/tcp/single-port",
      "profile": ".. profile name ..",
      
      // Transport type
      "transport": ".. transport name ..",
      
      // Upcounter name
      "upcounter": ".. upcounter name ..",
      
      // Event loop for server socket
      "parentEventLoopGroup": ".. master group name ..",
      
      // Event loop for client sockets
      "childEventLoopGroup": ".. child group name ..",
      
      // Pipeline that should process connection event
      "connectPipeline": ".. pipeline name ..",
      
      // Address where toind server socket
      "address": "host:port",
    }
    ```

* single-port UDP endpoint (`"netty/udp/single-port"`)

    Configuration example:
    
    ```JavaScript
    {
      "skeleton": "netty/server/tcp/single-port",
      "profile": ".. profile name ..",
      
      // Transport type
      "transport": ".. transport name ..",
      
      // Upcounter name
      "upcounter": ".. upcounter name ..",
      
      // Event loop to use
      "eventLoopGroup": ".. event loop group name ..",
      
      // Pipeline that should process endpoint initialization
      "setupPipeline": ".. pipeline name ..",
      
      // Address where toind server socket
      "address": "host:port",
    }
    ```

## Server address configuration

String passed as `"address"` parameter may contain hostname/address and port where server socket should be bound. But any part of the address (except the separator - `":"`) may be omitted:

* if hostname is omitted (e.g. `":8080"`) then server is bound to `0.0.0.0` address. This is normal behavior for most cases.

* if port is omitted (e.g. `"localhost:"`) then ephemeral (dynamic) port will be used.

* if both parts are omitted (and address is `":"`) then server will be bound to ephemeral port on address `0.0.0.0`.
