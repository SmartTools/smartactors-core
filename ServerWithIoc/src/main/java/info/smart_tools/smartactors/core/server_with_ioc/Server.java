package info.smart_tools.smartactors.core.server_with_ioc;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject_simple_implementation.IObjectImpl;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;

/**
 * Implementation {@link IServer} with scoped IOC
 */
public class Server implements IServer {

    @Override
    public void initialize()
            throws ServerInitializeException {
        try {
            initializeScopeProvider();
        } catch (Exception e) {
            throw new ServerInitializeException("Server Initialize failed.");
        }
    }

    @Override
    public void start()
            throws ServerExecutionException {
        try {


            /** Example of registration the Singleton strategy */
            Key key1 = new Key("a");
            IResolveDependencyStrategy strategy = new SingletonStrategy("abcd");
            IOC.register(key1, strategy);
            /** Example of usage the Singleton strategy */
            String result1 = IOC.resolve(key1);

            /** Example of registration the ResolveByNameIoc strategy */
            IOC.register(
                    IOC.getKeyForKeyStorage(),
                    new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
            );
            /** Example of usage the ResolveByNameIoc strategy */
            IKey key2 = IOC.resolve(IOC.getKeyForKeyStorage(), "key2");
            IKey key3 = IOC.resolve(IOC.getKeyForKeyStorage(), "key3");
            IKey key4 = IOC.resolve(IOC.getKeyForKeyStorage(), "key2");  // Should be same as key2

            /** Example of registration the CreateNewInstance strategy */
            IKey key5 = new Key("create_new");
            IOC.register(
                    key5,
                    new CreateNewInstanceStrategy(
                            (a) -> new IObjectImpl())
            );
            /** Example of usage the CreateNewInstance strategy */
            IObject test = IOC.resolve(key5);


            /** Example of registration and usage CreateNewInstance strategy by named key */
            Object param = new Object();
            IKey key6 = IOC.resolve(IOC.getKeyForKeyStorage(), "key6");
            IOC.register(
                    key6,
                    new CreateNewInstanceStrategy(
                            (a) -> new IObjectImpl())
            );
            IObject obj = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), "key6"), param);
            System.out.print("Did it.");


            /** Example of registration and usage Keys service locator */
            IKey key7 = Keys.getOrAdd("key7");
            IOC.register(
                    key7,
                    new CreateNewInstanceStrategy(
                            (a) -> {
                                return 2;
                            })
            );
            IOC.resolve(Keys.getOrAdd("key7"));

        } catch (Exception | Error e) {
            System.out.println(e);
        }
    }

    private void initializeScopeProvider()
            throws Exception {
//         /** Subscribe for java 1.7 or less */
//         ScopeProvider.subscribeOnCreationNewScope(new ScopeCreationEventHandler(IOC.getIocKey()));

        /** Subscribe for java 1.8 or high */
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );


        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);

    }

}

