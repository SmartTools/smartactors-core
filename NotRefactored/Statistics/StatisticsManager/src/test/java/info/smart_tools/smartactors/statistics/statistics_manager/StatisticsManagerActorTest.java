package info.smart_tools.smartactors.statistics.statistics_manager;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import info.smart_tools.smartactors.statistics.statistics_manager.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.statistics.statistics_manager.exceptions.CommandNotFoundException;
import info.smart_tools.smartactors.statistics.statistics_manager.wrappers.StatisticsCommandWrapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.verification.WantedButNotInvoked;

import java.text.MessageFormat;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for {@link StatisticsManagerActor}.
 */
public class StatisticsManagerActorTest extends PluginsLoadingTestBase {
    private StatisticsManagerActor actor;

    private IConfigurationManager configurationManagerMock;
    private IStrategy[] srs = new IStrategy[1];
    private ISensorHandle[] shs = new ISensorHandle[srs.length];

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        configurationManagerMock = mock(IConfigurationManager.class);
        IOC.register(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()), new SingletonStrategy(configurationManagerMock));

        for (int i = 0; i < srs.length; i++) {
            srs[i] = mock(IStrategy.class);
            shs[i] = mock(ISensorHandle.class);
            IOC.register(Keys.getKeyByName(MessageFormat.format("sensor type {0}", i)), srs[i]);
            when(srs[i].resolve(any(), any())).thenReturn(shs[i]);
        }

        IOC.register(Keys.getKeyByName("configuration object"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                try {
                    IObject obj = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), args);
                    obj.setValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "__is_config"), "1");
                    return (T) obj;
                } catch (ResolutionException | InvalidArgumentException | ChangeValueException e) {
                    throw new StrategyException(e);
                }
            }
        });

        IOC.register(Keys.getKeyByName("chain_id_from_map_name"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) String.valueOf(args[0]).concat("__0");
            }
        });

        doAnswer(invocation -> {
            assertEquals("1", invocation.getArgumentAt(0, IObject.class)
                    .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "__is_config")));

            return null;
        }).when(configurationManagerMock).applyConfig(any());
    }

    private Object c(final String cmd, final Object arg) throws Exception {
        StatisticsCommandWrapper message = mock(StatisticsCommandWrapper.class);

        when(message.getCommand()).thenReturn(cmd);
        when(message.getCommandArguments()).thenReturn(arg);

        actor.executeCommand(message);

        try {
            ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);

            verify(message).setCommandResult(resultCaptor.capture());

            return resultCaptor.getValue();
        } catch (WantedButNotInvoked e) {
            ArgumentCaptor<Throwable> exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);

            verify(message).setException(exceptionCaptor.capture());

            throw (Exception) exceptionCaptor.getValue();
        }
    }

    @Test(expected = CommandNotFoundException.class)
    public void Should_throwWhenInvalidCommandRequired()
            throws Exception {
        actor = new StatisticsManagerActor();
        c("bad", null);
    }

    @Test
    public void Should_createAndShutdownSensors()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        verify(srs[0], times(1)).resolve(any(), any());

        assertEquals("OK", c("shutdownSensor", "theSensor"));

        verifyNoMoreInteractions(srs[0]);
        verify(shs[0], times(1)).shutdown();
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_notCreateSensorsWithMatchingIdentifiers()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'c':'d'}," +
                        "'dependency':'sensor type 1'" +
                        "}").replace('\'','"')));
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_throwWhenTryingToShutdownNonExistSensor()
            throws Exception {
        actor = new StatisticsManagerActor();

        c("shutdownSensor", "theSensor");
    }

    @Test
    public void Should_enumerateSensors()
            throws Exception {
        actor = new StatisticsManagerActor();

        IObject creationArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'));

        assertEquals("OK", c("createSensor", creationArgs));

        List<IObject> list = (List) c("enumSensors", null);

        assertEquals(1, list.size());
        assertEquals("theSensor", list.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id")));
        assertEquals("sensor type 0", list.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency")));
        assertSame(
                creationArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args")),
                list.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args"))
        );
    }

    @Test
    public void Should_createCollectorObjects()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}," +
                        "'queryStepConfig':{" +
                        "'c':'d'" +
                        "}" +
                        "}").replace('\'','"'))));

        ArgumentCaptor<IObject> configCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(configurationManagerMock).applyConfig(configCaptor.capture());

        List<IObject> objectsSection = (List) configCaptor.getValue()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects"));
        assertEquals(1, objectsSection.size());
        IObject obj = objectsSection.get(0);

        assertEquals("the collector actor", obj.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency")));
        assertEquals("actor", obj.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "kind")));
        assertEquals("b", obj.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "a")));

        String objectId = String.valueOf(obj.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name")));

        assertTrue(objectId.startsWith("data-collector/theCollector-"));

        List<IObject> mapsSection = (List) configCaptor.getValue()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maps"));
        assertEquals(1, mapsSection.size());
        IObject map = mapsSection.get(0);

        assertEquals(
                c("getCollectorQueryChain", "theCollector"),
                map.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id"))
        );

        List<IObject> steps = (List<IObject>) map.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "steps"));

        assertEquals(2, steps.size());
        assertEquals("d", steps.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "c")));
        assertEquals(objectId, steps.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "target")));
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_notCreateCollectorsWithMatchingIdentifiers()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"')));
    }

    @Test
    public void Should_enumerateCollectors()
            throws Exception {
        actor = new StatisticsManagerActor();

        IObject creationArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'));

        assertEquals("OK", c("createCollector", creationArgs));

        List<IObject> list = (List) c("enumCollectors", null);

        assertEquals(1, list.size());

        IObject view = list.get(0);

        assertEquals(
                creationArgs.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args")),
                view.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args"))
        );
        assertEquals("theCollector", view.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id")));
    }

    @Test
    public void Should_addCollectorToSensorMapWhenLinkCommandCalled()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject linkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'theSensor'," +
                        "'collector':'theCollector'," +
                        "'stepConfig':{" +
                        "'a':'b'" +
                        "}" +
                        "}").replace('\'','"'));

        assertEquals("OK", c("link", linkArgs));

        ArgumentCaptor<IObject> configCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(configurationManagerMock).applyConfig(configCaptor.capture());

        List<IObject> mapsSection = (List) configCaptor.getValue()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maps"));
        assertEquals(1, mapsSection.size());

        List<IObject> steps = (List) mapsSection.get(0)
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "steps"));
        assertEquals(1, steps.size());

        assertTrue(String.valueOf(steps.get(0).getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "target")))
                .startsWith("data-collector/theCollector-"));
    }

    @Test
    public void Should_removeCollectorFromSensorMapWhenUnlinkCommandCalled()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        IObject linkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'theSensor'," +
                        "'collector':'theCollector'," +
                        "'stepConfig':{" +
                        "'a':'b'" +
                        "}" +
                        "}").replace('\'','"'));

        assertEquals("OK", c("link", linkArgs));

        reset(configurationManagerMock);

        assertEquals("OK", c("unlink", linkArgs));

        ArgumentCaptor<IObject> configCaptor = ArgumentCaptor.forClass(IObject.class);

        verify(configurationManagerMock).applyConfig(configCaptor.capture());

        List<IObject> mapsSection = (List) configCaptor.getValue()
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maps"));
        assertEquals(1, mapsSection.size());

        List<IObject> steps = (List) mapsSection.get(0)
                .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "steps"));
        assertEquals(0, steps.size());
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_notCreateLinkWhenSensorDoesNotExist()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject linkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'notTheSensor'," +
                        "'collector':'theCollector'," +
                        "'stepConfig':{" +
                        "'a':'b'" +
                        "}" +
                        "}").replace('\'','"'));

        c("link", linkArgs);
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_notCreateLinkWhenCollectorDoesNotExist()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject linkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'theSensor'," +
                        "'collector':'notTheCollector'," +
                        "'stepConfig':{" +
                        "'a':'b'" +
                        "}" +
                        "}").replace('\'','"'));

        c("link", linkArgs);
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_throwWhenUnlinkCalledWithNonExistSensorId()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject unlinkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'notTheSensor'," +
                        "'collector':'theCollector'" +
                        "}").replace('\'','"'));

        c("unlink", unlinkArgs);
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_throwWhenUnlinkCalledWithNonExistCollectorId()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject unlinkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'theSensor'," +
                        "'collector':'notYourCollector'" +
                        "}").replace('\'','"'));

        c("unlink", unlinkArgs);
    }

    @Test
    public void Should_returnNotOKWhenThereWasNoSuchLink()
            throws Exception {
        actor = new StatisticsManagerActor();

        assertEquals("OK", c("createSensor", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theSensor'," +
                        "'args':{'a':'b'}," +
                        "'dependency':'sensor type 0'" +
                        "}").replace('\'','"'))));

        assertEquals("OK", c("createCollector", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'id':'theCollector'," +
                        "'args':{" +
                        "'dependency':'the collector actor'," +
                        "'kind':'actor'," +
                        "'a':'b'" +
                        "}" +
                        ",'queryStepConfig':{}" +
                        "}").replace('\'','"'))));

        reset(configurationManagerMock);

        IObject unlinkArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'sensor':'theSensor'," +
                        "'collector':'theCollector'" +
                        "}").replace('\'','"'));

        assertNotEquals("OK", c("unlink", unlinkArgs));
    }
}
