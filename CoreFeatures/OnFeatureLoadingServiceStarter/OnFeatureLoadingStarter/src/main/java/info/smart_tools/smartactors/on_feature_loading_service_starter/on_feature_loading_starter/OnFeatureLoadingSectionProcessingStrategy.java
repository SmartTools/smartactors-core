package info.smart_tools.smartactors.on_feature_loading_service_starter.on_feature_loading_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.field_name_tools.FieldNames;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

import java.util.List;

/**
 * Initializes OnFeatureLoading using configuration.
 * <p>
 * Expects the following configuration format:
 * <p>
 * <pre>
 *     {
 *         "onFeatureLoading": [
 *                 {
 *                     "chain": "chain_name_1",
 *                     "revert": false,
 *                     "messages": [
 *                         {
 *                             "fieldName1": "value1",
 *                             "fieldName2": "value2",
 *                             ...
 *                         }, {
 *                             ...
 *                         }
 *                     ]
 *                 },
 *                 {
 *                     "chain": "chain_name_1",
 *                 }
 *                 // . . .
 *             ]
 *     }
 * </pre>
 */
public class OnFeatureLoadingSectionProcessingStrategy implements ISectionStrategy {

    private static final int DEFAULT_STACK_DEPTH = 5;

    private final IFieldName sectionNameFieldName;
    private final IFieldName chainFieldName;
    private final IFieldName messagesFieldName;
    private final IFieldName revertFieldName;
    private final IFieldName scopeSwitchingFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public OnFeatureLoadingSectionProcessingStrategy()
            throws ResolutionException {
        this.sectionNameFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "onFeatureLoading"
        );
        this.chainFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "chain"
        );
        this.revertFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "revert"
        );
        this.messagesFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "messages"
        );
        this.scopeSwitchingFieldName = FieldNames.getFieldNameByName("scopeSwitching");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            List<IObject> onFeatureLoadingConfig = (List<IObject>) config.getValue(this.sectionNameFieldName);
            IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName("task_queue"));

            Integer stackDepth;
            try {
                stackDepth = IOC.resolve(Keys.getKeyByName("default_stack_depth"));
            } catch (ResolutionException e) {
                stackDepth = DEFAULT_STACK_DEPTH;
            }

            for (IObject task : onFeatureLoadingConfig) {
                Boolean isRevert = (Boolean) task.getValue(this.revertFieldName);
                if (!isRevert) {
                    String chainName = (String) task.getValue(this.chainFieldName);
                    Boolean scopeSwitching = (Boolean) task.getValue(this.scopeSwitchingFieldName);
                    if (scopeSwitching == null) {
                        scopeSwitching = true;
                    }
                    List<IObject> messages = (List<IObject>) task.getValue(this.messagesFieldName);
                    for (IObject message : messages) {
                        IMessageProcessingSequence processingSequence = IOC.resolve(
                                IOC.resolve(
                                        IOC.getKeyForKeyByNameStrategy(),
                                        "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"
                                ),
                                stackDepth,
                                chainName,
                                message,
                                scopeSwitching
                        );
                        IMessageProcessor messageProcessor = IOC.resolve(
                                IOC.resolve(
                                        IOC.getKeyForKeyByNameStrategy(),
                                        "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"
                                ),
                                queue,
                                processingSequence
                        );
                        messageProcessor.process(
                                message,
                                (IObject) IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")
                                )
                        );
                    }
                }
            }
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | MessageProcessorProcessException e) {
            throw new ConfigurationProcessingException("Error occurred executing \"onFeatureLoading\" configuration section.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRevertConfig(final IObject config)
            throws ConfigurationProcessingException {
        ConfigurationProcessingException exception = new ConfigurationProcessingException("Error occurred reverting \"onFeatureLoading\" configuration section.");
        try {
            List<IObject> onFeatureLoadingConfig = (List<IObject>) config.getValue(this.sectionNameFieldName);
            IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName("task_queue"));

            Integer stackDepth;
            try {
                stackDepth = IOC.resolve(Keys.getKeyByName("default_stack_depth"));
            } catch (ResolutionException e) {
                stackDepth = DEFAULT_STACK_DEPTH;
            }

            for (IObject task : onFeatureLoadingConfig) {
                try {
                    Boolean isRevert = (Boolean) task.getValue(this.revertFieldName);
                    if (isRevert) {
                        String chainName = (String) task.getValue(this.chainFieldName);
                        Boolean scopeSwitching = (Boolean) task.getValue(this.scopeSwitchingFieldName);
                        if (scopeSwitching == null) {
                            scopeSwitching = true;
                        }
                        List<IObject> messages = (List<IObject>) task.getValue(this.messagesFieldName);
                        for (IObject message : messages) {
                            try {
                                IMessageProcessingSequence processingSequence = IOC.resolve(
                                        IOC.resolve(
                                                IOC.getKeyForKeyByNameStrategy(),
                                                "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"
                                        ),
                                        stackDepth,
                                        chainName,
                                        message,
                                        scopeSwitching
                                );
                                IMessageProcessor messageProcessor = IOC.resolve(
                                        IOC.resolve(
                                                IOC.getKeyForKeyByNameStrategy(),
                                                "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"
                                        ),
                                        queue,
                                        processingSequence
                                );
                                messageProcessor.process(
                                        message,
                                        (IObject) IOC.resolve(
                                                Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")
                                        )
                                );
                            } catch (InvalidArgumentException | ResolutionException | MessageProcessorProcessException | RuntimeException e) {
                                exception.addSuppressed(e);
                            }
                        }
                    }
                } catch (ReadValueException | InvalidArgumentException e) {
                    exception.addSuppressed(e);
                }
            }
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            exception.addSuppressed(e);
        }
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    @Override
    public IFieldName getSectionName() {
        return this.sectionNameFieldName;
    }
}
