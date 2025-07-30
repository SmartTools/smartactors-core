# System actors pack

## Actor collection receiver

Actor collection receiver contains a collection of child receivers and
routes received messages to them depending on a key specified in the
message. If receiver for a key in a received message is not found then
it creates new child receiver using it's description in current chain
step.

### Configuration example

In `"objects"` section:

```JSON
{
    "kind": "raw",
    "dependency": "ActorCollection",
    "name": "my-collection"
}
```

In chain step:
```JSON
{
    "target": "my-collection",
    // Name of handler of *child* receiver (if child receivers have multiple handlers)
    "handler": "someHandler",
    // Configuration of child receiver, just like in "objects" section
    "new": {
        // Kind will be automatically replaced by appropriate value
        // adding specific pipeline stages ("child_actor" for "actor")
        "kind": "actor",
        "dependency": "SomeMyActor",
        // Additional field, defining child deletion strategy.
        // optional, defaults to te value below
        "deletionCheckStrategy": "default child deletion check strategy"
    },
    // Wrapper configuration, as usual
    "wrapper": {
        // . . .
        // Additional getter to get child key, it still may be used
        // inside of child receivers
        "in_myKeyGetter": "message/theKey"
    },
    // Name of getter returning child key
    "key": "in_myKeyGetter"
}
```

### Deleting child receivers

Collection receiver creates additional pipeline stage that checks if
child receiver should be deleted after the _top level object_ (actor)
method returns. Strategy deciding if a child should be deleted should
implement `info.smart_tools.smartactors.system_actors_pack.actor_collection_receiver_interfaces.IChildDeletionCheckStrategy`
interface (it has one method accepting object creation context and raw
message environment).

Default strategy deletes the receiver if there is a deletion flag set in
message context (`"deleteChild"` field of message context is set to
`true`). You may easily set this flag in a child receiver if you have a
setter method in wrapper configured like the following:
```JSON
{
  "wrapper": {
    "out_setKillChild": "context/deleteChild",
    // . . .
  }
}
```
Default strategy clears the flag after reading it so setting it in one
child receiver will not destroy all the next child receivers the message
passes.

Notice: strategy will read flag after the top level object method
returns so it's undefined behavior if the flag is set in code executed
asynchronously.

## Object enumeration actor

TBD

## Repeater actor

TBD

## Shutdown actor

TBD
