package info.smart_tools.smartactors.endpoint.actor.response_sender_actor;

import info.smart_tools.smartactors.endpoint.actor.response_sender_actor.exceptions.ResponseSenderActorException;
import info.smart_tools.smartactors.endpoint.actor.response_sender_actor.wrapper.ResponseSenderMessage;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_content_strategy.IResponseContentStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Actor for sending response to client
 *
 * @deprecated
 */
@Deprecated
public class ResponseSenderActor {

    /**
     * Constructor for actor
     */
    public ResponseSenderActor() {
        System.out.println("[\033[1;33mWARNING\033[0m] \"ResponseSenderActor\" is deprecated. Use \"response sender receiver\" instead.");
    }

    /**
     * Handler of the actor for send response
     *
     * @param message Wrapper of the actor
     * @throws ResponseSenderActorException if there are some problems on sending response
     */
    // TODO: 21.07.16 Remake with using interface
    public void sendResponse(final ResponseSenderMessage message)
            throws ResponseSenderActorException {

        IObjectWrapper messageWrapper = (IObjectWrapper) message;

        //Get response IObject
        IFieldName responseFieldName = null;
        try {
            responseFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "response");
            IObject responseIObject = messageWrapper.getEnvironmentIObject(responseFieldName);

            //Create and fill full environment
            IObject environment = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
            IFieldName contextFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
            IFieldName httpResponseIsSentFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sendResponseOnChainEnd");
            IFieldName configFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "config");
            IFieldName messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
            IFieldName endpointName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "endpointName");
            environment.setValue(responseFieldName, responseIObject);
            environment.setValue(configFieldName, messageWrapper.getEnvironmentIObject(configFieldName));
            environment.setValue(contextFieldName, messageWrapper.getEnvironmentIObject(contextFieldName));
            environment.setValue(messageFieldName, messageWrapper.getEnvironmentIObject(messageFieldName));

            IFieldName channelFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "channel");
            IChannelHandler channelHandler = (IChannelHandler)
                    messageWrapper.getEnvironmentIObject(contextFieldName).getValue(channelFieldName);

            IResponse response = IOC.resolve(Keys.getKeyByName(IResponse.class.getCanonicalName()));
            IResponseContentStrategy contentStrategy =
                    IOC.resolve(Keys.getKeyByName(IResponseContentStrategy.class.getCanonicalName()), environment);
            contentStrategy.setContent(responseIObject, response);

            IResponseSender sender = IOC.resolve(Keys.getKeyByName(IResponseSender.class.getCanonicalName()),
                    IOC.resolve(Keys.getKeyByName("http_request_key_for_response_sender"), environment),
                    messageWrapper.getEnvironmentIObject(contextFieldName).getValue(endpointName));
            sender.send(response, environment, channelHandler);
            messageWrapper.getEnvironmentIObject(contextFieldName).setValue(httpResponseIsSentFieldName, true);
        } catch (Exception e) {
            throw new ResponseSenderActorException(e);
        }
    }
}