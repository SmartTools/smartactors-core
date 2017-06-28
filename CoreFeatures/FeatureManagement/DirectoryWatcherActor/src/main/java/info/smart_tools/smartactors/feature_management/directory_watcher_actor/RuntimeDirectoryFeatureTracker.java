package info.smart_tools.smartactors.feature_management.directory_watcher_actor;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.exception.WatchingServiceException;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.wrapper.StartWatchingWrapper;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.wrapper.StopWatchingWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;

/**
 * Actor that listens creation new files in the specific directory and puts location of new file to the message
 */
public class RuntimeDirectoryFeatureTracker {

    private IFieldName fileNameFieldName;
    private IFieldName observedDirectoryFieldName;

    private IReceiverChain executionChain;

    private IPath watchingDir;

    private Thread watchingThread;
    private WatchService watchingService;
    private IScope scope;

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
            this.watchingDir = new info.smart_tools.smartactors.base.path.Path(wrapper.getObservedDirectory());
            this.fileNameFieldName = IOC.resolve(
                    Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                    wrapper.getFileNameFieldName()
            );
            this.observedDirectoryFieldName = IOC.resolve(
                    Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                    wrapper.getObservedDirectoryFieldName()
            );
            resolveChainByChainName(wrapper.getExecutionChain());
            startWatchingService(this.watchingDir);
        } catch (ResolutionException | ChainNotFoundException | ReadValueException | ScopeProviderException e) {
            throw new WatchingServiceException(e);
        }
    }

    private void resolveChainByChainName(final String chainName) throws ResolutionException, ChainNotFoundException {
        Object chainId = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), "chain_id_from_map_name"), chainName
        );
        IChainStorage chainStorage = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IChainStorage.class.getCanonicalName())
        );
        this.executionChain = chainStorage.resolve(chainId);
    }

    private void startExecutionChain(final IPath newFilePath)
            throws ResolutionException, ChangeValueException, InvalidArgumentException, ScopeProviderException, MessageProcessorProcessException {
        if (!newFilePath.getPath().endsWith(".zip") && !newFilePath.getPath().endsWith(".json")) {
            return;
        }
        ScopeProvider.setCurrentScope(this.scope);
        IQueue queue = IOC.resolve(Keys.getOrAdd("task_queue"));
        Integer stackDepth = IOC.resolve(Keys.getOrAdd("default_stack_depth"));

        IMessageProcessingSequence processingSequence = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessingSequence.class.getCanonicalName()),
                stackDepth,
                this.executionChain
        );
        IMessageProcessor messageProcessor = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessor.class.getCanonicalName()),
                queue,
                processingSequence);
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        IObject message = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        message.setValue(this.fileNameFieldName, newFilePath);
        message.setValue(this.observedDirectoryFieldName, this.watchingDir);
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
            this.watchingThread = new Thread(task);
            this.watchingThread.setDaemon(true);
            this.watchingThread.start();
        } catch (IOException | InitializationException e) {
            throw new WatchingServiceException(e);
        }
    }
}
