package info.smart_tools.smartactors.database_postgresql_plugins.connection_options_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for resolving connection options from properties
 */
public class ConnectionOptionsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public ConnectionOptionsPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("PostgresConnectionOptionsPlugin")
    public void registerCanonizationStrategies()
            throws ResolutionException, InvalidArgumentException, RegistrationException {
        IField urlF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "url");
        IField usernameF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "username");
        IField passwordF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "password");
        IField maxConnectionsF = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "maxConnections");

        IOC.register(Keys.getKeyByName("PostgresConnectionOptionsStrategy"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    IObject opts = (IObject) args[0];
                    return new ConnectionOptions() {

                        @Override
                        public String getUrl() throws ReadValueException {
                            try {
                                return urlF.in(opts);
                            } catch (InvalidArgumentException e) {
                                return null;
                            }
                        }
                        @Override
                        public String getUsername() throws ReadValueException {
                            try {
                                return usernameF.in(opts);
                            } catch (InvalidArgumentException e) {
                                return null;
                            }
                        }
                        @Override
                        public String getPassword() throws ReadValueException {
                            try {
                                return passwordF.in(opts);
                            } catch (InvalidArgumentException e) {
                                return null;
                            }
                        }
                        @Override
                        public Integer getMaxConnections() throws ReadValueException {
                            try {
                                return maxConnectionsF.in(opts);
                            } catch (InvalidArgumentException e) {
                                return 1;
                            }
                        }
                        @Override
                        public void setUrl(final String url) throws ChangeValueException {
                        }
                        @Override
                        public void setUsername(final String username) throws ChangeValueException {
                        }
                        @Override
                        public void setPassword(final String password) throws ChangeValueException {
                        }
                        @Override
                        public void setMaxConnections(final Integer maxConnections) throws ChangeValueException {
                        }
                    };
                }
        ));
    }
}
