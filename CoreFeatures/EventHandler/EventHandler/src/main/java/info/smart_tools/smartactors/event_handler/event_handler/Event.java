package info.smart_tools.smartactors.event_handler.event_handler;

/**
 * The implementation of {@link IEvent}
 */
public final class Event implements IEvent {

    private Object body;
    private Object parameters;

    private Event() {
    }

    public Object getBody() {
        return body;
    }

    public Object getParameters() {
        return parameters;
    }

    /**
     * Inner builder for instance of {@link Event}
     * @return the instance of {@link Event}
     */
    public static Builder builder() {
        return new Event().new Builder();
    }

    /**
     * Implementation of factory for {@link Event} by builder pattern
     */
    public final class Builder {

        /**
         * Default constructor
         */
        private Builder() {
        }

        /**
         * sets the original event form
         * @param eventBody the original event form
         * @return the current state of builder
         */
        public Builder body(final Object eventBody) {
            Event.this.body = eventBody;

            return this;
        }

        /**
         * sets parameters which can be useful for event processing
         * @param eventParameters the original parameters
         * @return the current state of builder
         */
        public Builder parameters(final Object eventParameters) {
            Event.this.parameters = eventParameters;

            return this;
        }

        /**
         * Builds an instance of {@link Event}
         * @return the instance of {@link Event}
         */
        public IEvent build() {

            return Event.this;
        }
    }
}
