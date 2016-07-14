package info.smart_tools.smartactors.core.receiver_chain;

import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Strategy of creation of an {@link ImmutableReceiverChain}.
 *
 * Expected arguments:
 * - {@link Object} - identifier of the chain
 * - {@link IObject} describing the chain
 * - {@link IChainStorage} to look for exceptional chains
 * - {@link IRouter} to find the receivers
 * Expected format of description:
 * <pre>
 *     {
 *         "steps": [
 *             {                            // object describing a single step of message processing (a single receiver)
 *                                          // this object will be passed to {@link IMessageReceiver#receive} method
 *                                          // as second argument.
 *                 "target": "theTarget",   // (just for example) fields defining the receiver. concrete field names
 *                                          // depend on implementation of "receiver_id_from_iobject" strategy
 *             },
 *             {
 *                 . . .
 *             }
 *         ],
 *         "exceptional": [                 // exception chains in the order the exception classes should be checked
 *             {
 *                 "class": "java.lang.NullPointerException",   // exception class
 *                 "chain": "myExceptionalChain"                // exceptional chain name
 *             },
 *             {
 *                 "class": "org.my.Exception",
 *                 "chain": "myExceptionalChain2"
 *             }
 *         ]
 *     }
 * </pre>
 */
public class ImmutableReceiverChainResolutionStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            Object chainId = args[0];
            IObject description = (IObject) args[1];
            IChainStorage chainStorage = (IChainStorage) args[2];
            IRouter router = (IRouter) args[3];

            IKey fieldNameKey = IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName());
            IKey receiverIdKey = IOC.resolve(IOC.getKeyForKeyStorage(), "receiver_id_from_iobject");
            IKey chainIdKey = IOC.resolve(IOC.getKeyForKeyStorage(), "chain_id");
            IFieldName stepsFieldName = IOC.resolve(fieldNameKey, "steps");
            IFieldName exceptionalChainsFieldName = IOC.resolve(fieldNameKey, "exceptional");
            IFieldName exceptionClassFieldName = IOC.resolve(fieldNameKey, "class");
            IFieldName exceptionChainFieldName = IOC.resolve(fieldNameKey, "chain");

            List chainSteps = (List) description.getValue(stepsFieldName);
            List exceptionalChains = (List) description.getValue(exceptionalChainsFieldName);

            IMessageReceiver[] receivers = new IMessageReceiver[chainSteps.size()];
            IObject[] arguments = new IObject[chainSteps.size()];

            for (int i = 0; i < chainSteps.size(); i++) {
                IObject step = (IObject) chainSteps.get(i);

                receivers[i] = router.route(IOC.resolve(receiverIdKey, step));
                arguments[i] = step;
            }

            LinkedHashMap<Class<? extends Throwable>, IReceiverChain> exceptionalChainsMap = new LinkedHashMap<>();

            for (Object chainDesc : exceptionalChains) {
                IObject desc = (IObject) chainDesc;

                Class<?> clazz = this.getClass().getClassLoader().loadClass(String.valueOf(desc.getValue(exceptionClassFieldName)));
                IReceiverChain chain = chainStorage.resolve(IOC.resolve(chainIdKey, desc.getValue(exceptionChainFieldName)));

                exceptionalChainsMap.put((Class<? extends Throwable>) clazz, chain);
            }

            return (T) new ImmutableReceiverChain(chainId.toString(), receivers, arguments, exceptionalChainsMap);
        } catch (ChainNotFoundException | ClassNotFoundException | ResolutionException | ReadValueException |
                RouteNotFoundException | InvalidArgumentException e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
