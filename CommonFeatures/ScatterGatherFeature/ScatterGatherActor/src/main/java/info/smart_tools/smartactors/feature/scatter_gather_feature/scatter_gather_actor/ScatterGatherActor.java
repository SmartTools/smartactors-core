package info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature.scatter_gather_feature.iscatter_gather_strategy.IScatterGatherStrategy;
import info.smart_tools.smartactors.feature.scatter_gather_feature.iscatter_gather_strategy.exception.IScatterGatherStrategyException;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.exception.ScatterGatherActorException;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper.GatherWrapper;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper.ScatterWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

import java.util.Collection;

/**
 * Actor, that realize scatter-gather pattern
 */
public class ScatterGatherActor {
    private final IScatterGatherStrategy strategy;
    private IObject scatterGatherInfo;
    private IMessageProcessor messageProcessor;


    public ScatterGatherActor(IObject args) throws ResolutionException, ReadValueException, InvalidArgumentException {
        IFieldName strategyDependencyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "strategyDependency");
        String strategyDependency = (String) args.getValue(strategyDependencyFieldName);
        this.scatterGatherInfo = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        strategy = IOC.resolve(Keys.getOrAdd(strategyDependency));
    }

    public void scatter(ScatterWrapper wrapper) throws ScatterGatherActorException {
        try {
            Collection<Object> scatterCollection = wrapper.getCollection();
            messageProcessor = wrapper.getMessageProcessor();
            strategy.beforeScatter(messageProcessor, scatterGatherInfo);
            for (Object object : scatterCollection) {
                try {
                    Object chainName = strategy.chainChoose(object);
                    Object replyChainName = strategy.chooseReplyChain(object);
                    IObject sendingObject = strategy.formIObjectToSentFromObject(object);
                    MessageBus.sendAndReply(sendingObject, chainName, replyChainName);
                    strategy.onMessageSent(messageProcessor, scatterGatherInfo);
                } catch (SendingMessageException e) {
                    strategy.onMessageSendFail(messageProcessor, scatterGatherInfo);
                }
            }
            strategy.afterScatter(messageProcessor, scatterGatherInfo);
        } catch (ReadValueException | IScatterGatherStrategyException e) {
            throw new ScatterGatherActorException(e);
        }
    }

    public void gather(GatherWrapper wrapper) throws ScatterGatherActorException {
        try {
            IObject result = wrapper.getResult();
            strategy.onGather(messageProcessor, result, scatterGatherInfo);
        } catch (IScatterGatherStrategyException | ReadValueException e) {
            throw new ScatterGatherActorException(e);
        }
    }


}
