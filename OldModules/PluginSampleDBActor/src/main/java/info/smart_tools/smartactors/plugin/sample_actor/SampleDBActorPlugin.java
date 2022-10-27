package info.smart_tools.smartactors.plugin.sample_actor;

import info.smart_tools.smartactors.actors.SampleDBActor;
import info.smart_tools.smartactors.actors.exception.SampleDBException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Plugin for register {@link SampleDBActor} creation strategy.
 * Also it registers the strategy which reads Postgres connection options from 'db_connection.properties' file.
 */
public class SampleDBActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public SampleDBActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Load the plugin for {@link SampleDBActor}
     * @throws PluginException Throw when plugin can't be load
     */
    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("SampleDBActorPlugin");

            item
                .process(() -> {
                try {
                    IOC.register(Keys.getKeyByName("PostgresConnectionOptions"), new IStrategy() {
                        @Override
                        public ConnectionOptions resolve(Object... args) throws StrategyException {
                            Properties connectionProperties = new Properties();
                            try {
                                connectionProperties.load(new FileReader("db_connection.properties"));
                                return new ConnectionOptions() {
                                    @Override
                                    public String getUrl() throws ReadValueException {
                                        return connectionProperties.getProperty("url");
                                    }
                                    @Override
                                    public String getUsername() throws ReadValueException {
                                        return connectionProperties.getProperty("username");
                                    }
                                    @Override
                                    public String getPassword() throws ReadValueException {
                                        return connectionProperties.getProperty("password");
                                    }
                                    @Override
                                    public Integer getMaxConnections() throws ReadValueException {
                                        return Integer.parseInt(connectionProperties.getProperty("maxConnections", "1"));
                                    }
                                    @Override
                                    public void setUrl(String url) throws ChangeValueException {
                                    }
                                    @Override
                                    public void setUsername(String username) throws ChangeValueException {
                                    }
                                    @Override
                                    public void setPassword(String password) throws ChangeValueException {
                                    }
                                    @Override
                                    public void setMaxConnections(Integer maxConnections) throws ChangeValueException {
                                    }
                                };
                            } catch (IOException e) {
                                throw new StrategyException("Cannot read db_connection.properties", e);
                            }
                        }
                    });
                    IOC.register(Keys.getKeyByName("SampleDBActor"), new IStrategy() {
                        @Override
                        public SampleDBActor resolve(Object... args) throws StrategyException {
                            try {
                                return new SampleDBActor();
                            } catch (SampleDBException e) {
                                throw new StrategyException(e);
                            }
                        }
                    });
                } catch (ResolutionException | RegistrationException e) {
                    throw new ActionExecutionException(e);
                }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load SampleDBActorPlugin plugin", e);
        }
    }
}
