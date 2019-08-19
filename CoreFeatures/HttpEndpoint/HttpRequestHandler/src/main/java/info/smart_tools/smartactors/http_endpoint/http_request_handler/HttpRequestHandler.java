package info.smart_tools.smartactors.http_endpoint.http_request_handler;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.iup_counter.IUpCounter;
import info.smart_tools.smartactors.base.iup_counter.exception.UpCounterCallbackExecutionException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.endpoint.endpoint_handler.EndpointHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.IAddRequestParametersToIObject;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.RequestHandlerDataException;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.RequestHandlerInternalException;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.IResponseSender;
import info.smart_tools.smartactors.endpoint.interfaces.iresponse_sender.exceptions.ResponseSendingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.iscope.IScope;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Endpoint handler for HTTP requests.
 * It should parse request and send response if there are some problems on parsing.
 */
public class HttpRequestHandler extends EndpointHandler<ChannelHandlerContext, FullHttpRequest> {
    private final String name;

    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;
    private static final int NOT_FOUND_ERROR_STATUS_CODE = 404;

    private final IFieldName messageFieldName;
    private final IFieldName contextFieldName;
    private final IFieldName finalActionsFieldName;
    private final IFieldName httpResponseIsSentFieldName;
    private final IFieldName httpResponseStatusCodeFieldName;
    private final IFieldName accessForbiddenFieldName;
    private final IFieldName requestFieldName;
    private final IFieldName channelFieldName;
    private final IFieldName headersFieldName;
    private final IFieldName cookiesFieldName;
    private final IFieldName endpointName;
    private final IFieldName responseStrategyName;

    private boolean isShuttingDown = false;

    /**
     * Constructor for HttpRequestHandler
     *
     * @param scope              scope for HttpRequestHandler
     * @param module             the id of feature in which context HttpRequestHandler works
     * @param environmentHandler handler for environment
     * @param receiverName       chain name of chain that should receive message
     * @param name               name of the endpoint
     * @param upCounter          up-counter to use to subscribe on shutdown request
     */
    public HttpRequestHandler(
            final IScope scope, final IModule module, final IEnvironmentHandler environmentHandler, final Object receiverName,
            final String name, final IUpCounter upCounter) throws ResolutionException, UpCounterCallbackExecutionException {
        super(receiverName, environmentHandler, scope, module, name);
        this.name = name;

        messageFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message");
        contextFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context");
        finalActionsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "finalActions");
        httpResponseIsSentFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sendResponseOnChainEnd");
        httpResponseStatusCodeFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "httpResponseStatusCode");
        accessForbiddenFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "accessToChainForbiddenError");
        requestFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "request");
        channelFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "channel");
        headersFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "headers");
        cookiesFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "cookies");
        endpointName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "endpointName");
        responseStrategyName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responseStrategy");

        upCounter.onShutdownRequest(this.toString(), mode -> isShuttingDown = true);
    }

    @Override
    protected IObject getEnvironment(final ChannelHandlerContext ctx, final FullHttpRequest request)
            throws RequestHandlerDataException, RequestHandlerInternalException, ReadValueException {
        try {
            IObject message = IOC.resolve(Keys.getKeyByName("EmptyIObject"));
            if (!request.method().toString().equals("GET")) {
                IDeserializeStrategy deserializeStrategy = IOC.resolve(
                        Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"),
                        IOC.resolve(Keys.getKeyByName("http_request_key_for_deserialize"), request),
                        name
                );

                //resolving body of the request
                try {
                    message = deserializeStrategy.deserialize(request);
                } catch (DeserializationException e) {
                    IObject exception = IOC.resolve(Keys.getKeyByName("HttpPostParametersToIObjectException"));
                    ctx.writeAndFlush(formExceptionalResponse(exception));
                    throw new RequestHandlerDataException(e);
                }
            }

            //resolving uri and another request parameters of the request
            IAddRequestParametersToIObject requestParametersToIObject = IOC.resolve(
                    Keys.getKeyByName("info.smart_tools.smartactors.endpoint.interfaces.ideserialize_strategy.IDeserializeStrategy"),
                    "HTTP_GET",
                    name
            );
            try {
                requestParametersToIObject.extract(message, request);
            } catch (Exception e) {
                IObject exceptionalResponse = IOC.resolve(Keys.getKeyByName("HttpRequestParametersToIObjectException"));

                throw new RequestHandlerDataException(e);
            }

            IObject environment = IOC.resolve(Keys.getKeyByName("EmptyIObject"));

            IChannelHandler channelHandler = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.http_endpoint.channel_handler_netty.ChannelHandlerNetty"), ctx);
            //create context of the MP
            IObject context = IOC.resolve(Keys.getKeyByName("EmptyIObject"));
            context.setValue(channelFieldName, channelHandler);
            context.setValue(cookiesFieldName, new ArrayList<IObject>());
            context.setValue(headersFieldName, new ArrayList<IObject>());
            context.setValue(requestFieldName, request);
            context.setValue(endpointName, name);
            context.setValue(responseStrategyName, IOC.resolve(Keys.getKeyByName("endpoint response strategy")));

            context.setValue(httpResponseIsSentFieldName, false);
            IAction<IObject> httpFinalAction = new IAction<IObject>() {
                @Override
                public void execute(final IObject environment) throws ActionExecutionException, InvalidArgumentException {
                    try {
                        IObject context = (IObject) environment.getValue(contextFieldName);
                        if (null != context) {
                            if ((boolean) context.getValue(httpResponseIsSentFieldName)) {
                                return;
                            }
                        }
                        IFieldName channelFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "channel");
                        IChannelHandler channelHandler = (IChannelHandler)
                                context.getValue(channelFieldName);

                        IResponse response = IOC.resolve(Keys.getKeyByName(IResponse.class.getCanonicalName()));
                        response.setContent("".getBytes());

                        IResponseSender sender = IOC.resolve(Keys.getKeyByName(IResponseSender.class.getCanonicalName()),
                                IOC.resolve(Keys.getKeyByName("http_request_key_for_response_sender"), environment),
                                name);
                        // ToDo: need refactoring. Need create hashMap - errorName to statusCode
                        Boolean accessForbidden = (Boolean) context.getValue(accessForbiddenFieldName);
                        if (accessForbidden != null && accessForbidden) {
                            context.setValue(httpResponseStatusCodeFieldName, NOT_FOUND_ERROR_STATUS_CODE);
                        } else {
                            context.setValue(httpResponseStatusCodeFieldName, INTERNAL_SERVER_ERROR_STATUS_CODE);
                        }
                        sender.send(response, environment, channelHandler);
                    } catch (ResolutionException | ReadValueException | ResponseSendingException | ChangeValueException e) {
                        throw new ActionExecutionException("Could not execute final http action.");
                    }
                }
            };

            ArrayList<IAction<IObject>> finalActions = new ArrayList<>();
            finalActions.add(httpFinalAction);
            context.setValue(finalActionsFieldName, finalActions);

            //create environment
            environment.setValue(messageFieldName, message);
            environment.setValue(contextFieldName, context);
            return environment;
        } catch (InvalidArgumentException | SerializeException | ChangeValueException | ResolutionException e) {
            try {
                IObject exception = IOC.resolve(Keys.getKeyByName("HttpInternalException"), e);
                ctx.writeAndFlush(formExceptionalResponse(exception));
                throw new RequestHandlerInternalException(e);
            } catch (SerializeException | ResolutionException | InvalidArgumentException e1) {
                throw new RequestHandlerInternalException("Failed to send response", e);
            }
        } finally {
            request.release();
        }
    }

    @Override
    protected void sendExceptionalResponse(final ChannelHandlerContext ctx, final FullHttpRequest request,
                                           final IObject responseIObject) throws SerializeException,
            ReadValueException, InvalidArgumentException, ResolutionException {
        ctx.writeAndFlush(formExceptionalResponse(responseIObject));
    }

    private FullHttpResponse formExceptionalResponse(final IObject iObjectResponse)
            throws SerializeException, ResolutionException, ReadValueException, InvalidArgumentException {
        IFieldName exceptionalStatusCode = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "statusCode");
        ByteBuf byteResponse = Unpooled.wrappedBuffer(((String) iObjectResponse.serialize()).getBytes(Charset.forName("UTF-8")));
        int length = ((String) iObjectResponse.serialize()).length();
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf((Integer) iObjectResponse.getValue(exceptionalStatusCode)),
                        byteResponse);
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, length);
        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        return response;
    }

    @Override
    public void handle(final ChannelHandlerContext ctx, final FullHttpRequest request) throws ExecutionException {
        if (isShuttingDown) {
            try {
                sendExceptionalResponse(ctx, request, IOC.resolve(Keys.getKeyByName("HttpShuttingDownException")));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            request.retain();
            try {
                super.handle(ctx, request);
            } catch (ExecutionException | RuntimeException e) {
                request.release();
                throw e;
            }
        }
    }
}
