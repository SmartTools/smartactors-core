/**
 * This package contains two {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler
 * handlers}: {@link info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers.BindRequestToChannelHandler}
 * and {@link info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers.StoreBoundRequestHandler}.
 *
 * <p>
 *  These handlers are meant to be used in client endpoints working in request-response mode where each connection
 *  processes at most one request at time: {@code BindRequestToChannelHandler} binds a request to channel and
 *  {@code StoreBoundRequestHandler} stores a request bound to the channel in {@code "request"} field of the internal
 *  inbound message environment.
 * </p>
 */
package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;
