package info.smart_tools.smartactors.system_actors_pack.actor_collection_receiver;

import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link IMessageReceiver}.
 * Specific kind of receiver that provides creation of actors collection and its usage by keys.
 * * Expected format of config section (example):
 * <pre>
 * {
 *     objects: {
 *     . . .
 *        {
 *            "kind": "raw",
 *            "dependency": "ActorCollection",
 *            "name": "my-actor-collection"
 *        },
 *     . . .
 *     },
 *     . . .
 *     {
 *         {
 *             "target": "my-actor-collection",
 *             "handler": "transformAndPutForResponse",
 *             "new": {
 *                 "kind": "actor",
 *                 "dependency": "SampleActor"
 *             },
 *             "key": "in_actorId",
 *             "wrapper": {
 *                 "in_actorId": "message/ActorId",
 *                 "in_getSomeField": "message/Field",
 *                 "out_setSomeValueForRequest": "response/TransformedField",
 *                 "out_setCurrentActorState": "response/CurrentState",
 *                 "in_resetState": "message/Reset"
 *             }
 *         }
 *     }
 *     . . .
 * }
 * </pre>
 */
public class ActorCollectionReceiver implements IMessageReceiver {
    private static class ReceiverListener implements IReceiverObjectListener {
        private IMessageReceiver receiver;
        private boolean finished;

        @Override
        public void acceptItem(final Object itemId, final Object item)
                throws ReceiverObjectListenerException, InvalidReceiverPipelineException, InvalidArgumentException {
            if (null == item) {
                throw new InvalidArgumentException("Item is null.");
            }

            IMessageReceiver receiverItem;

            try {
                receiverItem = (IMessageReceiver) item;
            } catch (ClassCastException e) {
                throw new InvalidReceiverPipelineException("Item is not a message receiver.", e);
            }

            if (null != receiver) {
                throw new InvalidReceiverPipelineException("Collection receiver supports only objects with single external receiver.");
            }

            receiver = receiverItem;
        }

        @Override
        public void endItems()
                throws ReceiverObjectListenerException, InvalidReceiverPipelineException {
            if (null == receiver) {
                throw new InvalidReceiverPipelineException("No items passed to collection receiver.");
            }

            finished = true;
        }

        public IMessageReceiver getReceiver() throws InvalidReceiverPipelineException {
            if (!finished) {
                throw new InvalidReceiverPipelineException("Object creation not finished synchronously.");
            }

            return receiver;
        }
    }

    private IFieldName keyFieldName;
    private IFieldName newFieldName;

    private Map<Object, IMessageReceiver> childReceivers;

    /**
     * The constructor.
     *
     * @param childStorage    map to use to store child receivers
     * @throws InvalidArgumentException if {@code childStorage} is {@code null}
     * @throws ResolutionException if error occurs resolving any dependency
     */
    public ActorCollectionReceiver(final Map<Object, IMessageReceiver> childStorage)
            throws InvalidArgumentException, ResolutionException {
        if (null == childStorage) {
            throw new InvalidArgumentException("Child storage map is null.");
        }

        this.childReceivers = childStorage;

        this.keyFieldName  = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "key");
        this.newFieldName  = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "new");
    }

    private IMessageReceiver createReceiver(final IObject stepConf)
            throws ResolutionException, ReadValueException, InvalidArgumentException, ReceiverObjectListenerException,
                ReceiverObjectCreatorException , InvalidReceiverPipelineException {
        IObject newObjectConfig = (IObject) stepConf.getValue(newFieldName);
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        IReceiverObjectCreator creator = IOC.resolve(Keys.getOrAdd("full receiver object creator"), newObjectConfig);

        ReceiverListener listener = new ReceiverListener();

        creator.create(listener, stepConf, context);

        return listener.getReceiver();
    }

    private IMessageReceiver resolveReceiver(final Object id, final IObject stepConf)
            throws MessageReceiveException {
        IMessageReceiver receiver = childReceivers.get(id);

        if (null != receiver) {
            return receiver;
        }

        try {
            return childReceivers.computeIfAbsent(id, __ -> {
                try {
                    return createReceiver(stepConf);
                } catch (ResolutionException | ReadValueException | InvalidArgumentException | ReceiverObjectListenerException |
                        ReceiverObjectCreatorException | InvalidReceiverPipelineException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new MessageReceiveException(e);
        }
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException, AsynchronousOperationException {
        try {
            IObject stepConf = processor.getSequence().getCurrentReceiverArguments();

            IFieldName fieldNameForKeyName = IOC.resolve(
                    Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                    stepConf.getValue(this.keyFieldName));

            Object id =  processor.getEnvironment().getValue(fieldNameForKeyName);

            IMessageReceiver child = resolveReceiver(id, stepConf);

            try {
                child.receive(processor);
            } catch (MessageReceiveException e) {
                throw new MessageReceiveException(
                        MessageFormat.format("Error occurred in child receiver for key \"{0}\"", id), e);
            }
        } catch (ReadValueException | ResolutionException | InvalidArgumentException e) {
            throw new MessageReceiveException("Could not execute ActorCollectionReceiver.receive.", e);
        }
    }
}
