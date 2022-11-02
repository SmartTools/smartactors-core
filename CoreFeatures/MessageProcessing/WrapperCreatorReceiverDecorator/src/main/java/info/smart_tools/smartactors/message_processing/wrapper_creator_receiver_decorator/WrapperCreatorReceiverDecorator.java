package info.smart_tools.smartactors.message_processing.wrapper_creator_receiver_decorator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

import java.util.Map;

/**
 * Decorator for message receiver that wraps a message environment with a wrapper object if it's configuration is present in current step
 * arguments.
 */
public class WrapperCreatorReceiverDecorator implements IMessageReceiver {
    private final IMessageReceiver underlyingReceiver;
    private final IFieldName wrapperFieldName;
    private final Map<Object, IStrategy> wrapperStrategies;
    private final IKey strategyDependencyKey;

    /**
     * The constructor.
     *
     * @param underlyingReceiver    the receiver to decorate
     * @param wrapperStrategiesMap  map to use as a cache for wrapper object resolution strategies. This probably should be a map storing
     *                              keys by weak references
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public WrapperCreatorReceiverDecorator(
            final IMessageReceiver underlyingReceiver,
            final Map<Object, IStrategy> wrapperStrategiesMap,
            final String strategyDependencyName)
            throws ResolutionException {
        this.underlyingReceiver = underlyingReceiver;
        this.wrapperStrategies = wrapperStrategiesMap;

        this.strategyDependencyKey = Keys.getKeyByName(strategyDependencyName);

        wrapperFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "wrapper");
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException, AsynchronousOperationException {
        try {
            IObject stepArgs = processor.getSequence().getCurrentReceiverArguments();
            IObject wrapperConfigObject = (IObject) stepArgs.getValue(wrapperFieldName);

            if (null != wrapperConfigObject) {
                // stepArgs object is used as key for strategy cache as if the wrapperConfigObject was used the entry would not be ever
                // garbage-collected (even if wrapperStrategies stores keys by weak references): wrapper resolution strategy has a strong
                // reference to configuration object
                IStrategy conf = wrapperStrategies.get(stepArgs);

                if (null == conf) {
                    conf = IOC.resolve(strategyDependencyKey, wrapperConfigObject);
                    wrapperStrategies.put(stepArgs, conf);
                }

                processor.pushEnvironment(conf.resolve(processor.getEnvironment()));
            }

            underlyingReceiver.receive(processor);
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | StrategyException e) {
            throw new MessageReceiveException(e);
        }
    }

    @Override
    public void dispose() {
        try {
            underlyingReceiver.dispose();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
