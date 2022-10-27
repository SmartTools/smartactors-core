package info.smart_tools.smartactors.ioc_strategy_pack_plugins.datetime_formatter_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PluginDateTimeFormatterTest {
    static Bootstrap bootstrap;

    @BeforeClass
    public static void prepareIOC()
            throws PluginException, ProcessExecutionException, RevertProcessExecutionException, InvalidArgumentException {

        bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new PluginDateTimeFormatter(bootstrap).load();
        bootstrap.start();
    }

    @Test
    public void ShouldReturnFormatter() throws Exception {

        LocalDateTime now = LocalDateTime.of(2016, 8, 18, 0, 0);
        DateTimeFormatter formatter = IOC.resolve(Keys.getKeyByName("datetime_formatter"));
        assertEquals(formatter.format(now), "08-18-2016 00:00:00");
    }

    @Test
    public void ShouldRevertPlugin() throws Exception {

        bootstrap.revert();

        LocalDateTime now = LocalDateTime.of(2016, 8, 18, 0, 0);
        try {
            IOC.resolve(Keys.getKeyByName("datetime_formatter"));
            fail();
        } catch(ResolutionException e) { }

        bootstrap.start();
    }
}
