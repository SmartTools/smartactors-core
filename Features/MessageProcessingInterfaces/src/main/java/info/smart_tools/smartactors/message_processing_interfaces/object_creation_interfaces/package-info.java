/**
 * Package contains interfaces for components related to creation of receiver objects.
 *
 * A receiver object (logical receiver object) is canonically described by a configuration (represented as an {@link
 * info.smart_tools.smartactors.iobject.iobject.IObject}) containing list of actions that should be applied to the configuration to get
 * created all the receivers representing that logical object e.g.: resolve a user object, create invokers for it's methods, join them using
 * a router-receiver, wrap them with actor synchronization receiver, add some filters, add a proxy that will redirect messages if the actor
 * migrates away.
 *
 * The term "pipeline" is for such sequence of actions.
 */
package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces;
