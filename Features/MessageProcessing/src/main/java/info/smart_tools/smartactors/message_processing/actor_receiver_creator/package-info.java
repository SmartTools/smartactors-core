/**
 * Package contains implementation of {@link info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator}.
 * Create chain of receivers:
 * Generates {@code HandlerReceivers} foreach method and puts its to new
 * instance of {@link info.smart_tools.smartactors.message_processing.handler_routing_receiver.HandlerRoutingReceiver}.
 * After that puts instance
 * of {@link info.smart_tools.smartactors.message_processing.handler_routing_receiver.HandlerRoutingReceiver}
 * to the new instance of {@link info.smart_tools.smartactors.message_processing.actor_receiver.ActorReceiver}.
 */
package info.smart_tools.smartactors.message_processing.actor_receiver_creator;