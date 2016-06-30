package info.smart_tools.smartactors.plugin.postgres_connection_pool;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.plugin.postges_connection_pool.PostgresConnectionPoolPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, PostgresConnectionPoolPlugin.class})
@RunWith(PowerMockRunner.class)
public class PostgresConnectionPoolPluginTest {
    private PostgresConnectionPoolPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);

        IKey key1 = mock(IKey.class);
        IKey keyPool = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key1);
        when(IOC.resolve(eq(key1), eq("PostgresConnectionPool"))).thenReturn(keyPool);

        bootstrap = mock(IBootstrap.class);
        plugin = new PostgresConnectionPoolPlugin(bootstrap);
    }

    @Test
    public void Should() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("PostgresConnectionPoolPlugin").thenReturn(bootstrapItem);
        plugin.load();
        verifyNew(BootstrapItem.class).withArguments("PostgresConnectionPoolPlugin");
        verify(bootstrap).add(eq(bootstrapItem));
    }
}
