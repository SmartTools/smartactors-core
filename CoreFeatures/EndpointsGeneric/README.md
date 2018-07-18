# Generic endpoint skeletons  feature

This feature contains endpoint skeletons independent of endpoint implementation.

The following skeletons are provided by this feature:

* Endpoint that creates a global outbound channel (`"generic/outbound"`)
    
    Configuration example:
    
    ```JavaScript
    {
      "skeleton": "generic/outbound",
      "profile": ".. profile name ..",
      
      // Identifier of the global channel
      "channelId": ".. channel identifier ..",
      
      // Identifier of the pipeline.
      // All outbound messages will be sent to this pipeline
      // with no connection context
      "pipeline": ".. pipeline name ..",
      
      // Endpoint will unregister a global channel when
      // shutdown of a subsystem managed by this upcounter
      // will be completed
      "upcounter": ".. upcounter name .."
    }
    ```

