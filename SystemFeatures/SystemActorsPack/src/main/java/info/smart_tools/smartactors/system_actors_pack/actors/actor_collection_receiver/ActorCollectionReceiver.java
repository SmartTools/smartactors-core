package info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import info.smart_tools.smartactors.system_actors_pack.actors.actor_collection_receiver.pipeline.CollectionReceiverReceiverListener;

import java.text.MessageFormat;
import java.util.Map;

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
 *                 "deletionCheckStrategy": "default child deletion check strategy",
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

    private final IFieldName keyFieldName;
    private final IFieldName newFieldName;
    private final IFieldName deletionActionFieldName;

    private Map<Object, IMessageReceiver> childReceivers;

    private final IAction<IObject> deletionAction;

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

        this.keyFieldName  = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "key");
        this.newFieldName  = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "new");
        this.deletionActionFieldName  = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "deletionAction");

        this.deletionAction = ctx -> {
            try {
                childReceivers.remove(ctx.getValue(keyFieldName));
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new ActionExecutionException(e);
            }
        };
    }

    private IMessageReceiver createReceiver(final IObject stepConf, final Object id)
        throws ResolutionException, ReadValueException, InvalidArgumentException, ReceiverObjectListenerException,
        ReceiverObjectCreatorException, InvalidReceiverPipelineException, ChangeValueException {
        IObject newObjectConfig = (IObject) stepConf.getValue(newFieldName);

        IObject context = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        context.setValue(keyFieldName, id);
        context.setValue(deletionActionFieldName, deletionAction);

        IReceiverObjectCreator creator = IOC.resolve(Keys.getKeyByName("full receiver object creator"), newObjectConfig);

        CollectionReceiverReceiverListener listener = new CollectionReceiverReceiverListener();

        creator.create(listener, newObjectConfig, context);

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
                    return createReceiver(stepConf, id);
                } catch (ResolutionException | ReadValueException | InvalidArgumentException | ReceiverObjectListenerException |
                         ReceiverObjectCreatorException | InvalidReceiverPipelineException | ChangeValueException e) {
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
                Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
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

    @Override
    public void dispose() {
        // TODO: implement
    }
}
