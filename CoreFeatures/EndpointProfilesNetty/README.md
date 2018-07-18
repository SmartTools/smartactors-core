# Default profiles for Netty-based endpoints

This feature contains some basic endpoint profiles.

## TCP single-port server endpoint

This profile is registered with name `"netty/server/tcp-base"`.

## SSL enabled endpoint

This profile is a mixin for server TCP endpoint that forces it to enable SSL. Registered with name `"netty/server/ssl"`.

Configuration example:

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

## HTTP server endpoint

Basic HTTP endpoint with Web-socket support. Registered with name `"netty/server/http"`.

HTTP endpoint uses default mime decoders table to decode HTTP requests. HTTP responses are encoded using JSON encoder (this behaviour may be changed by overriding `"fill-response"` pipeline in inherited profile).

Inbound text Web-socket frames are decoded as JSON documents, binary frames are ignored (will cause error). Override `"decode-websocket-text"` and `"decode-websocket-binary"` to change this behaviour. Outbound messages are encoded as text frames containing JSON documents (override `"send-websocket/build-frame"` to change behaviour).

Configuration example:

```JavaScript
{
  "skeleton": "netty/server/tcp/single-port",
  "profile": "netty/server/http",

  "parentEventLoopGroup": "defaultServerParent",
  "childEventLoopGroup": "defaultServerChild",

  "transport": "prefer-native",
  "upcounter": "root",

  "connectPipeline": "accept-client",

  // Server address ":8080" means port 8080 at all interfaces
  "address": ":8080",

  // Chain for inbound HTTP messages
  "mainInboundChain": "httpInboundChain",

  // Chain for inbound Web-socket messages
  "webSocketInboundChain": "webSocketInboundChain",

  // Path of Web-socket
  "webSocketPath": "/ws",

  // Path templates
  "httpPathTemplates": [
    "/ws"
  ]
}
```

## HTTPS server endpoint

The HTTP endpoint with SSL mixin. Registered with name `"netty/server/https"`.

Configuration example:

```JavaScript
{
  "skeleton": "netty/server/tcp/single-port",
  "profile": "netty/server/https",

  /* .. the parameters of HTTP endpoint .. */

  // Certificate path
  "serverCertificate": "/etc/ssl/server.crt",

  // Private key file path. File must have PKCS#8 format.
  "serverCertificateKey": "/etc/ssl/server.key",

  // Private key password
  "serverCertificateKeyPassword": "password"
}
```
