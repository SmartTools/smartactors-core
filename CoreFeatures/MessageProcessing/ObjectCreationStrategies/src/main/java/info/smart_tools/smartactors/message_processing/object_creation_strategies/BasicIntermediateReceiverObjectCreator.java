package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;

import java.util.Collection;

/**
 * Base class for {@link IReceiverObjectCreator object creators} that are responsible for creation of intermediate steps of receiver
 * pipeline such as filters, proxies, method invokers.
 */
public abstract class BasicIntermediateReceiverObjectCreator implements IReceiverObjectCreator, IReceiverObjectListener {
    private final IReceiverObjectCreator underlyingObjectCreator;
    private IReceiverObjectListener listener;
    private IObject objectConfig, context, filterConfig;

    protected final IReceiverObjectListener getListener() throws ReceiverObjectListenerException {
        return listener;
    }

    protected final IReceiverObjectCreator getUnderlyingCreator() throws ReceiverObjectCreatorException {
        return underlyingObjectCreator;
    }

    protected final IObject getContext() {
        return context;
    }

    protected final IObject getObjectConfig() {
        return objectConfig;
    }

    protected final IObject getFilterConfig() {
        return filterConfig;
    }

    /**
     * The constructor.
     *
     * @param underlyingObjectCreator   {@link IReceiverObjectCreator} that will create underlying object(s)
     * @param filterConfig              configuration of the step of pipeline
     * @param objectConfig              configuration of the object
     */
    public BasicIntermediateReceiverObjectCreator(
            final IReceiverObjectCreator underlyingObjectCreator,
            final IObject filterConfig,
            final IObject objectConfig
    ) {
        this.underlyingObjectCreator = underlyingObjectCreator;
        this.filterConfig = filterConfig;
        this.objectConfig = objectConfig;
    }

    @Override
    public void create(final IReceiverObjectListener listener, final IObject config, final IObject context)
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        this.listener = listener;
        this.context = context;
        underlyingObjectCreator.create(this, config, context);
    }

    @Override
    public Collection<Object> enumIdentifiers(final IObject config, final IObject context)
            throws InvalidReceiverPipelineException, ReceiverObjectCreatorException {
        return getUnderlyingCreator().enumIdentifiers(config, context);
    }

    @Override
    public void endItems()
            throws ReceiverObjectListenerException, InvalidReceiverPipelineException {
        getListener().endItems();
    }
}
