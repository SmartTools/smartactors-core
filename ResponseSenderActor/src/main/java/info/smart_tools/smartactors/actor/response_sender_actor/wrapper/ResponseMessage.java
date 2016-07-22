package info.smart_tools.smartactors.actor.response_sender_actor.wrapper;

import info.smart_tools.smartactors.actor.response_sender_actor.ResponseSenderActor;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Wrapper for {@link ResponseSenderActor}
 */
public interface ResponseMessage {
    /**
     *
     * @return channel that should use for send response
     */
    IChannelHandler getChannelHandler();

    /**
     *
     * @return response object from message processor
     */
    IObject getResponse();

}
