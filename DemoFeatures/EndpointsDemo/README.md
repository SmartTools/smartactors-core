# Endpoints demo project

This demo project shows how to use HTTP client and server endpoints.

To see how it works send the following message to `"ping"` chain through HTTP server endpoint at port `8080` using any HTTP client:

```json
{
  "messageMapId": "ping",
  "url": "http://localhost:8080/mm/echo",
  "body": {
    "hello":"world",
    "messageMapId": "echo"
  }
}
```

or using curl:

```
curl http://localhost:8080/mm/ping    \
  -H 'content-type: application/json' \
  -d '{
    "url": "http://localhost:8080/mm/echo",
    "body": {
      "hello":"world",
      "messageMapId": "echo"
    }}' \
  -vv
```

You should see a response like this:

```json
{"received2":{"received1":{"messageMapId":"echo","hello":"world"}}}
```

The following happens when you send the response:

1) HTTP server endpoint receives a request and sends a message to `"ping"` chain
2) HTTP client endpoint sends a request containing value of `"body"` field of the original request to the server endpoint using URL `"http://localhost:8080/mm/echo"` (of course you may use anther URL to send a request to any other server but do not try to use `"http://localhost:8080/mm/ping"`)
3) HTTP server endpoint receives the second request and sends a message to `"echo"` chain
4) response to the second request containing content of request in `"received1"` field is sent by server and received by client endpoint
5) server sends a response with content of response to second request in `"received2"` field


