package info.smart_tools.smartactors.endpoint.endpoint_external_configuration_file_reader;

import info.smart_tools.smartactors.endpoint.interfaces.iendpoint_external_configuration_reader.IEndpointExternalConfigurationReader;
import info.smart_tools.smartactors.endpoint.interfaces.iendpoint_external_configuration_reader.exception.EndpointExternalConfigurationReaderException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class EndpointExternalConfigurationFileReaderTest extends IOCInitializer {
    private IEndpointExternalConfigurationReader reader;
    private IObject configuration;
    private IFieldName pathToExternalConfigurationFN;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void init() throws Exception {
        configuration = IOC.resolve(Keys.getKeyByName(IObject.class.getCanonicalName()));
        pathToExternalConfigurationFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "pathToExternalConfiguration");
    }

    @Test
    public void correctPropertiesFileTest() throws Exception {
        configuration.setValue(pathToExternalConfigurationFN, getAbsolutePathToFile("correct.properties"));
        reader = new EndpointExternalConfigurationFileReader(configuration);
        Assert.assertEquals("mainHttpEp", reader.read("name"));
        Assert.assertEquals("defaultValue", reader.readOrDefault("nonExistField", "defaultValue"));
    }

    @Test
    public void incorrectPropertiesFileTest() throws Exception {
        configuration.setValue(pathToExternalConfigurationFN, getAbsolutePathToFile("incorrect.properties"));
        reader = new EndpointExternalConfigurationFileReader(configuration);
        Assert.assertNull(reader.read("name"));
    }

    @Test (expected = EndpointExternalConfigurationReaderException.class)
    public void propertiesFileNotExistTest() throws Exception {
        configuration.setValue(pathToExternalConfigurationFN, getAbsolutePathToFile("non-exist.properties"));
        reader = new EndpointExternalConfigurationFileReader(configuration);
    }

    @Test (expected = EndpointExternalConfigurationReaderException.class)
    public void fieldWithPathIsAbsentTest() throws Exception {
        configuration.deleteField(pathToExternalConfigurationFN);
        reader = new EndpointExternalConfigurationFileReader(configuration);
    }

    @Test (expected = EndpointExternalConfigurationReaderException.class)
    public void configurationIsNullTest() throws Exception {
        reader = new EndpointExternalConfigurationFileReader(null);
    }

    private String getAbsolutePathToFile(String fileName) {
        Path resourceDirectory = Paths.get("src","test", "resources", fileName);
        return resourceDirectory.toFile().getAbsolutePath();
    }
}
