package info.smart_tools.smartactors.endpoint_plugins.endpoint_external_configuration_file_reader_plugin;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;

public class EndpointExternalConfigurationFileReaderPluginTest extends IOCInitializer {
    private final String classKey = "endpoint external configuration file reader";

    @Override
    protected void registry(String... strings) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    private EndpointExternalConfigurationFileReaderPlugin plugin;
    private String propsFilePath;

    @Before
    public void init() {
        plugin = new EndpointExternalConfigurationFileReaderPlugin(null);
        ClassLoader classLoader = getClass().getClassLoader();
        propsFilePath = Objects.requireNonNull(classLoader.getResource("props.properties")).getFile();
    }


    @Test(expected = ResolutionException.class)
    public void testResolutionExceptionNotRegisteredFilePropertiesReader() throws Exception {
        IOC.resolve(Keys.getKeyByName(classKey));
    }


    @Test(expected = ResolutionException.class)
    public void testResolutionExceptionNoArgsFilePropertiesReader() throws Exception {
        plugin.init();
        assertNotNull(IOC.resolve(Keys.getKeyByName(classKey)));
    }


    @Test(expected = ResolutionException.class)
    public void testResolutionExceptionFileNotFoundFilePropertiesReader() throws Exception {
        plugin.init();
        assertNotNull(IOC.resolve(Keys.getKeyByName(classKey), "non-existing-file"));
    }

    @Test
    public void testResolveFromIoc() throws Exception {
        plugin.init();
        assertNotNull(IOC.resolve(Keys.getKeyByName(classKey), propsFilePath));
    }

    @Test(expected = ResolutionException.class)
    public void testResolveBeforeInitial() throws Exception {
        IOC.resolve(Keys.getKeyByName(classKey), "non-existing-file");
    }

    @Test(expected = ResolutionException.class)
    public void testUnregister() throws Exception {
        plugin.init();
        assertNotNull(IOC.resolve(Keys.getKeyByName(classKey), propsFilePath));
        plugin.unregister();
        assertNotNull(IOC.resolve(Keys.getKeyByName(classKey), propsFilePath));
    }
}
