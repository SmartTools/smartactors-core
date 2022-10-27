package info.smart_tools.smartactors.testing.test_http_endpoint;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implementation of {@link IAsyncService}.
 * Imitates work of Http Endpoint and uses {@link ISource} as data source
 * as data source.
 */
public class TestHttpEndpoint implements IAsyncService {

    private ISource dataSource;
    private IScope scope;
    private IEnvironmentHandler handler;
    private Long timeInterval;
    private IAction<IObject> rule;
    private ExecutorService executorService;

    private final IFieldName contentFieldName;
    private final IFieldName chainFieldName;
    private final IFieldName callbackFieldName;


    /**
     * Creates instance of {@link TestHttpEndpoint}.
     * @param source the instance of {@link ISource}
     * @param testScope the test scope
     * @param testHandler the handler for processing test message
     * @param timeBetweenTests the interval between starting processing next message
     * @param transformationRule the rule for transform incoming message to FullHttpRequest.
     *          If {@code null} the default strategy will be used.
     * @throws InvalidArgumentException if one of the arguments is incorrect
     * @throws InitializationException if any error was occurred
     */
    public TestHttpEndpoint(
            final ISource source,
            final IScope testScope,
            final IEnvironmentHandler testHandler,
            final Long timeBetweenTests,
            final IAction<IObject> transformationRule
            ) throws InvalidArgumentException, InitializationException {
        if (null == source) {
            throw new InvalidArgumentException("The source should not be null.");
        }
        if (null == testScope) {
            throw new InvalidArgumentException("The scope should not be null.");
        }
        if (null == testHandler) {
            throw new InvalidArgumentException("The handler should not be null.");
        }
        this.rule = transformationRule;
        if (null == transformationRule) {
            this.rule = defaultTransformationRule();
        }
        this.dataSource = source;
        this.scope = testScope;
        this.handler = testHandler;
        this.timeInterval = timeBetweenTests;
        this.executorService = Executors.newSingleThreadExecutor();
        try {
            this.contentFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "content"
            );
            this.chainFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainName"
            );
            this.callbackFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "callback"
            );
        } catch (ResolutionException e) {
            throw new InitializationException("Could not create new instance of TestHttpEndpoint.", e);
        }
    }

    @Override
    public CompletableFuture start() {
        return CompletableFuture.supplyAsync(this::execute, this.executorService);
    }

    @Override
    public CompletableFuture stop() {
        return CompletableFuture.runAsync(() -> this.executorService.shutdown()).thenApply(a -> TestHttpEndpoint.this);
    }

    private Future<TestHttpEndpoint> execute() {
        try {
            while (true) {
                ScopeProvider.setCurrentScope(this.scope);
                try {
                    IObject obj = (IObject) dataSource.next();
                    if (obj != null) {
                        this.rule.execute(obj);
                        handler.handle(
                                (IObject) obj.getValue(this.contentFieldName),
                                obj.getValue(this.chainFieldName),
                                (IAction<Throwable>) obj.getValue(this.callbackFieldName)
                        );
                    }
                    Thread.sleep(timeInterval);
                } catch (Throwable e) {
                    //System.out.println();
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("ScopeProvider is not initialized.");
        }
    }

    private IAction<IObject> defaultTransformationRule() {
        return (iObject) -> {
            try {
                IFieldName environmentFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "environment"
                );
                IFieldName contextFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"
                );
                IFieldName requestFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "request"
                );
                IFieldName channelFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "channel"
                );
                IFieldName headersFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "headers"
                );
                IFieldName cookiesFieldName = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "cookies"
                );
                IObject content = (IObject) iObject.getValue(this.contentFieldName);
                IObject environment = (IObject) content.getValue(environmentFieldName);

                IObject context = (IObject) environment.getValue(contextFieldName);
                if (null == context) {
                    context = IOC.resolve(
                            IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject")
                    );
                }

                IChannelHandler channelHandler = IOC.resolve(
                        IOC.resolve(IOC.getKeyForKeyByNameStrategy(), TestChannelHandler.class.getCanonicalName()));
                context.setValue(channelFieldName, channelHandler);

                context.setValue(cookiesFieldName, new ArrayList<IObject>());
                context.setValue(headersFieldName, new ArrayList<IObject>());
                TestFullHttpRequest request = new TestFullHttpRequest((IObject) environment.getValue(requestFieldName));
                context.setValue(requestFieldName, request);

                environment.setValue(contextFieldName, context);

            } catch (ChangeValueException | ResolutionException | ReadValueException e) {
                throw new RuntimeException("Execution of DefaultTransformationRule has been failed.");
            }
        };
    }
}