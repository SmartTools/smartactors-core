package info.smart_tools.smartactors.create_postgres_collection_if_not_exists_feature.create_collection_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public final class TestConnectionOptionsPlugin extends BootstrapPlugin {
    protected TestConnectionOptionsPlugin(IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("TestConnectionOptionsPlugin")
    @After({})
    @Before("")
    public void register() throws InvalidArgumentException, ResolutionException, RegistrationException {
        IOC.register(Keys.getOrAdd("PostgresConnectionOptions"), new ApplyFunctionToArgumentsStrategy(args -> new ConnectionOptions() {
            @Override
            public String getUrl() throws ReadValueException {
                return null;
            }

            @Override
            public String getUsername() throws ReadValueException {
                return null;
            }

            @Override
            public String getPassword() throws ReadValueException {
                return null;
            }

            @Override
            public Integer getMaxConnections() throws ReadValueException {
                return null;
            }

            @Override
            public void setUrl(String s) throws ChangeValueException {

            }

            @Override
            public void setUsername(String s) throws ChangeValueException {

            }

            @Override
            public void setPassword(String s) throws ChangeValueException {

            }

            @Override
            public void setMaxConnections(Integer integer) throws ChangeValueException {

            }
        }));
    }
}
