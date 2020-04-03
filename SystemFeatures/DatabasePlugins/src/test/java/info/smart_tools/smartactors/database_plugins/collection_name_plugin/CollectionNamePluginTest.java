package info.smart_tools.smartactors.database_plugins.collection_name_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, IActionNoArgs.class, ResolveByNameIocStrategy.class, CollectionNamePlugin.class, CollectionName.class})
@RunWith(PowerMockRunner.class)
public class CollectionNamePluginTest {

    private CollectionNamePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);
        mockStatic(CollectionName.class);

        IKey keyGeneral = mock(IKey.class);
        IKey keyPlugin = mock(IKey.class);
        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(keyGeneral);
        when(IOC.resolve(eq(keyGeneral), eq("CollectionNamePlugin"))).thenReturn(keyPlugin);

        bootstrap = mock(IBootstrap.class);
        plugin = new CollectionNamePlugin(bootstrap);
    }

    @Test
    public void ShouldCorrectLoadPlugin() throws Exception {

        IKey collectionNameKey = mock(IKey.class);
        when(Keys.getKeyByName(CollectionName.class.getCanonicalName())).thenReturn(collectionNameKey);

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        HashMap<String, CollectionName> collectionMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(collectionMap);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CollectionNamePlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        ArgumentCaptor<ResolveByNameIocStrategy> createNewInstanceStrategyArgumentCaptor =
            ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);
        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        IOC.register(eq(collectionNameKey), createNewInstanceStrategyArgumentCaptor.capture());

        String collectionNameStr = "collectionName";

        CollectionName collectionName = mock(CollectionName.class);
        when(CollectionName.fromString(collectionNameStr)).thenReturn(collectionName);

        assertEquals(createNewInstanceStrategyArgumentCaptor.getValue().resolve(collectionNameStr), collectionName);

        verifyStatic();
        CollectionName.fromString(eq(collectionNameStr));

        verify(bootstrap).add(bootstrapItem);
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapItemThrowsException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
    }

    @Test(expected = ActionExecutionException.class)
    public void ShouldThrowRuntimeException_When_LambdaThrowsException() throws Exception {

        when(Keys.getKeyByName(CollectionName.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CollectionNamePlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        HashMap<String, CollectionName> collectionMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(collectionMap);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CollectionNamePlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());
        actionArgumentCaptor.getValue().execute();
    }
}
