/**
 * EventHandler and its components.
 * Purpose: receiving, storing and processing different events (messages, exceptions, etc) from actor system components.
 * Main principles of implementation:
 * - service-locator pattern;
 * - EventHandler contains a container of a implementations of the {@link info.smart_tools.smartactors.event_handler.event_handler.IEventHandler} (event handlers).
 * On calling method {@link info.smart_tools.smartactors.event_handler.event_handler.EventHandler#handle(info.smart_tools.smartactors.event_handler.event_handler.IEvent)}
 * these event handlers should be executed in reversed registration order (from later to earlier).
 * If the next handler has been executed successfully the chain of a call of handlers will be stopped;
 */
package info.smart_tools.smartactors.event_handler.event_handler;