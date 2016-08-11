package info.smart_tools.smartactors.core.http_environment_extractor;

import info.smart_tools.smartactors.core.ideserialize_strategy.IDeserializeStrategy;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.ideserialize_strategy.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ienvironment_extractor.IEnvironmentExtractor;
import info.smart_tools.smartactors.core.ienvironment_extractor.exceptions.EnvironmentExtractionException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.ArrayList;

/**
 * Environment extractor for http request
 */
public class HttpEnvironmentExtractor implements IEnvironmentExtractor {
    private IFieldName messageFieldName;
    private IFieldName contextFieldName;
    private IFieldName requestFieldName;
    private IFieldName channelFieldName;
    private IFieldName headersFieldName;
    private IFieldName cookiesFieldName;

    /**
     * Constructor for environment extractor
     *
     * @throws EnvironmentExtractionException if there are some problems on resolving field name
     */
    public HttpEnvironmentExtractor() throws EnvironmentExtractionException {
        try {
            messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
            contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
            requestFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request");
            channelFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channel");
            headersFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
            cookiesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "cookies");
        } catch (ResolutionException e) {
            throw new EnvironmentExtractionException("An exception occurred on resolving field name", e);
        }
    }

    @Override
    public IObject extract(final Object request, final Object ctx) throws EnvironmentExtractionException {
        IDeserializeStrategy deserializeStrategy = null;
        IObject message = null;
        try {
            deserializeStrategy = IOC.resolve(Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()), request);
        } catch (ResolutionException e) {
            throw new EnvironmentExtractionException("An exception occurred on resolving deserialize strategy", e);
        }
        try {
            message = deserializeStrategy.deserialize(request);
        } catch (DeserializationException e) {
            throw new EnvironmentExtractionException("An exception occurred on request deserialization", e);
        }
        IObject environment = null;
        IObject context = null;
        try {
            environment = IOC.resolve(Keys.getOrAdd("EmptyIObject"));
            context = IOC.resolve(Keys.getOrAdd("EmptyIObject"));
        } catch (ResolutionException e) {
            throw new EnvironmentExtractionException("An exception occurred on resolving IObject from IOC", e);
        }
        IChannelHandler channelHandler = null;
        try {
            channelHandler = IOC.resolve(Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName()), ctx);
        } catch (ResolutionException e) {
            throw new EnvironmentExtractionException("An exception occured on resolving channel handler", e);
        }

        try {
            //create context of the MP
            context.setValue(channelFieldName, channelHandler);
            context.setValue(requestFieldName, request);
            context.setValue(headersFieldName, new ArrayList<>());
            context.setValue(cookiesFieldName, new ArrayList<>());
            //create environment
            environment.setValue(messageFieldName, message);
            environment.setValue(contextFieldName, context);
        } catch (InvalidArgumentException | ChangeValueException e) {
            throw new EnvironmentExtractionException("An exception occurred on creating environment", e);
        }
        return environment;
    }
}