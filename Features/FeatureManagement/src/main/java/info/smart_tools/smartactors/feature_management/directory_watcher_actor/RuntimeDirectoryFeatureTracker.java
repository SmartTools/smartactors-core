package info.smart_tools.smartactors.feature_management.directory_watcher_actor;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.exception.WatchingServiceException;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.wrapper.StartWatchingWrapper;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.wrapper.StopWatchingWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;

/**
 * Actor that listens creation new files in the specific directory and puts location of new file to the message
 */
public class RuntimeDirectoryFeatureTracker {

    private IFieldName fileNameFieldName;
    private IFieldName observedDirectoryFieldName;

    private String executionChainName;

    private IPath watchingDir;

    private Thread watchingThread;
    private WatchService watchingService;
    private IScope scope;
    private IModule module;

    private final static String EXTENSION_SEPARATOR = ".";
    private final static String TASK_QUEUE_IOC_NAME = "task_queue";
    private final static String CHAIN_ID_STORAGE_STRATEGY_NAME = "chain_id_from_map_name_and_message";
    private final static String IOBJECT_FACTORY_STRATEGY_NAME = "info.smart_tools.smartactors.iobject.iobject.IObject";
    private final static String FIELD_NAME_FACTORY_STARTEGY_NAME =
            "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
    private final static String MESSAGE_PROCESSOR_SEQUENCE_FACTORY_STRATEGY_NAME =
            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence";
    private final static String MESSAGE_PROCESSOR_FACTORY_STRATEGY_NAME =
            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor";

    //TODO: this parameters would be took out into the config.json as actor arguments
    private final static List<String> FILE_TYPE_LIST = Arrays.asList("zip", "jar", "json");

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on IOC resolution
     */
    public RuntimeDirectoryFeatureTracker()
            throws ResolutionException {
    }

    /**
     * Stops directory watching service
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws WatchingServiceException if any errors occurred on stopping service
     */
    public void stopService(final StopWatchingWrapper wrapper)
            throws WatchingServiceException {
        try {
            this.watchingService.close();
        } catch (IOException e) {
            throw new WatchingServiceException(e);
        }
    }

    /**
     * Starts directory watching service
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws WatchingServiceException if any errors occurred on watching directory
     */
    public void startService(final StartWatchingWrapper wrapper)
            throws WatchingServiceException {
        try {
            this.scope = ScopeProvider.getCurrentScope();
            this.module = ModuleManager.getCurrentModule();
            this.watchingDir = new info.smart_tools.smartactors.base.path.Path(wrapper.getObservedDirectory());
            this.fileNameFieldName = IOC.resolve(
                    Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), wrapper.getFileNameFieldName()
            );
            this.observedDirectoryFieldName = IOC.resolve(
                    Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), wrapper.getObservedDirectoryFieldName()
            );
            this.executionChainName = wrapper.getExecutionChain();
            startWatchingService(this.watchingDir);
        } catch (ResolutionException | ReadValueException | ScopeProviderException e) {
            throw new WatchingServiceException(e);
        }
    }

    private void startExecutionChain(final IPath newFilePath)
            throws ResolutionException, ChangeValueException, InvalidArgumentException,
            ScopeProviderException, MessageProcessorProcessException, ChainNotFoundException {
        if (!FILE_TYPE_LIST.contains(getExtension(new File(newFilePath.getPath())))) {
            return;
        }
        ScopeProvider.setCurrentScope(this.scope);
        ModuleManager.setCurrentModule(this.module);

        IObject context = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME));
        IObject message = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME));
        message.setValue(this.fileNameFieldName, newFilePath);
        message.setValue(this.observedDirectoryFieldName, this.watchingDir);

        Integer stackDepth = IOC.resolve(Keys.getKeyByName("default_stack_depth"));
        IMessageProcessingSequence processingSequence = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), MESSAGE_PROCESSOR_SEQUENCE_FACTORY_STRATEGY_NAME),
                stackDepth,
                this.executionChainName,
                message
        );

        IQueue queue = IOC.resolve(Keys.getKeyByName(TASK_QUEUE_IOC_NAME));
        IMessageProcessor messageProcessor = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), MESSAGE_PROCESSOR_FACTORY_STRATEGY_NAME),
                queue,
                processingSequence
        );
        messageProcessor.process(message, context);
    }

    private void startWatchingService(final IPath watchingDirectory)
            throws WatchingServiceException {
        try {
            Path nioPath = Paths.get(watchingDirectory.getPath());
            this.watchingService = nioPath.getFileSystem().newWatchService();
            Runnable task = new ListeningTask(this.watchingService, watchingDirectory, (a) -> {
                try {
                    this.startExecutionChain(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            this.watchingThread = new Thread(task, "RuntimeDirectoryFeatureTracker");
            this.watchingThread.setDaemon(true);
            this.watchingThread.start();
        } catch (IOException | InitializationException e) {
            throw new WatchingServiceException(e);
        }
    }

    private String getExtension(final File f) {
        return f.getName().substring(f.getName().lastIndexOf(EXTENSION_SEPARATOR) + 1);
    }
}
