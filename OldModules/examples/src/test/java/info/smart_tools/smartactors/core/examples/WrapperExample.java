package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.examples.actor.GreetingMessage;
import info.smart_tools.smartactors.core.examples.wrapper.ConcatSplitRulesPlugin;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.iobject_extension.configuration_object.ConfigurationObject;
import info.smart_tools.smartactors.iobject_extension.wds_object.WDSObject;
import info.smart_tools.smartactors.iobject_extension_plugins.configuration_object_plugin.InitializeConfigurationObjectStrategies;
import info.smart_tools.smartactors.iobject_extension_plugins.wds_object_plugin.PluginWDSObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.fieldname_plugin.FieldNamePlugin;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_standard_types_strategies_plugin.ResolveStandardTypesStrategiesPlugin;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.exception.WrapperGeneratorException;
import info.smart_tools.smartactors.message_processing_plugins.wrapper_generator_plugin.RegisterWrapperGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Some examples to use Wrapper.
 */
public class WrapperExample {

    private static IKey iObjectKey;
    private static IKey iFieldKey;
    private static IKey configObjectKey;
    private static IKey wdsObjectKey;
    private static IKey iWrapperGeneratorKey;

    @BeforeClass
    public static void loadPlugins() throws PluginException, ProcessExecutionException, ResolutionException, InvalidArgumentException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new FieldNamePlugin(bootstrap).load();
        new IFieldPlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        new ResolveStandardTypesStrategiesPlugin(bootstrap).load();
        new InitializeConfigurationObjectStrategies(bootstrap).load();
        new PluginWDSObject(bootstrap).load();
        new RegisterWrapperGenerator(bootstrap).load();
        new ConcatSplitRulesPlugin(bootstrap).load();
        bootstrap.start();

        iObjectKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        iFieldKey = Keys.getKeyByName(IField.class.getCanonicalName());
        configObjectKey = Keys.getKeyByName("configuration object");
        wdsObjectKey = Keys.getKeyByName(WDSObject.class.getCanonicalName());
        iWrapperGeneratorKey = Keys.getKeyByName(IWrapperGenerator.class.getCanonicalName());
    }

    @Test
    public void testConfigNormalization() throws ResolutionException, SerializeException, ReadValueException, InvalidArgumentException {
        ConfigurationObject configObject = IOC.resolve(configObjectKey,
                "{" +
                "\"in_getName\": \"message/personName\"," +
                "\"out_setGreeting\": \"response/greeting\"" +
                "}");

        IField getNameField = IOC.resolve(iFieldKey, "in_getName");
        IField setGreetingField = IOC.resolve(iFieldKey, "out_setGreeting");
        List<IObject> getNameObjects = getNameField.in(configObject);
        List<List<IObject>> setGreetingObjects = setGreetingField.in(configObject);

        IField nameField = IOC.resolve(iFieldKey, "name");
        IField argsField = IOC.resolve(iFieldKey, "args");

        assertEquals("wds_getter_strategy", nameField.in(getNameObjects.get(0)));
        List<String> getNameArgs = argsField.in(getNameObjects.get(0));
        assertEquals("message/personName", getNameArgs.get(0));

        assertEquals("wds_target_strategy", nameField.in(setGreetingObjects.get(0).get(0)));
        List<String> setGreetingArgs = argsField.in(setGreetingObjects.get(0).get(0));
        assertEquals("local/value", setGreetingArgs.get(0));
        assertEquals("response/greeting", setGreetingArgs.get(1));
    }

    @Test
    public void testConfigNormalizationForTarget() throws ResolutionException, SerializeException, ReadValueException, InvalidArgumentException {
        ConfigurationObject configObject = IOC.resolve(configObjectKey,
                "{" +
                "\"out_setName\": [[" +
                "{" +
                "\"name\": \"split_strategy\"," +
                "\"args\": [ \"local/value\", \"const/ \" ]" +
                "}," +
                "{" +
                "\"name\": \"target\"," +
                "\"args\": [ \"response/namesList\" ]" +
                "}" +
                "]]" +
                "}");

        IField setNameField = IOC.resolve(iFieldKey, "out_setName");
        List<List<IObject>> setNameObjects = setNameField.in(configObject);

        IField nameField = IOC.resolve(iFieldKey, "name");
        IField argsField = IOC.resolve(iFieldKey, "args");

        assertEquals("split_strategy", nameField.in(setNameObjects.get(0).get(0)));
        List<String> setNameSplitArgs = argsField.in(setNameObjects.get(0).get(0));
        assertEquals("local/value", setNameSplitArgs.get(0));
        assertEquals("const/ ", setNameSplitArgs.get(1));

        assertEquals("wds_target_strategy", nameField.in(setNameObjects.get(0).get(1)));
        List<String> setNameTargetArgs = argsField.in(setNameObjects.get(0).get(1));
        assertEquals("local/value", setNameTargetArgs.get(0));
        assertEquals("response/namesList", setNameTargetArgs.get(1));
    }

    @Test
    public void testWDSObject() throws ResolutionException, ReadValueException, ChangeValueException, InvalidArgumentException {
        IObject config = IOC.resolve(configObjectKey,
                "{" +
                "\"in_getName\": \"message/personName\"," +
                "\"out_setGreeting\": \"response/greeting\"" +
                "}");
        WDSObject wdsObject = IOC.resolve(wdsObjectKey, config);

        IObject environment = IOC.resolve(iObjectKey,
                "{" +
                "\"message\": { \"personName\": \"Ivan\" }," +
                "\"response\": {}" +
                "}");
        wdsObject.init(environment);

        IField getNameField = IOC.resolve(iFieldKey, "in_getName");
        assertEquals("Ivan", getNameField.in(wdsObject));

        IField setGreetingField = IOC.resolve(iFieldKey, "out_setGreeting");
        setGreetingField.out(wdsObject, "Hello");

        IField responseField = IOC.resolve(iFieldKey, "response");
        IObject response = responseField.in(environment);
        IField greetingField = IOC.resolve(iFieldKey, "greeting");
        assertEquals("Hello", greetingField.in(response));
    }

    @Test
    public void testWrapperGenerator() throws ResolutionException, WrapperGeneratorException, InvalidArgumentException, ReadValueException, ChangeValueException {
        IWrapperGenerator generator = IOC.resolve(iWrapperGeneratorKey);
        GreetingMessage message = generator.generate(GreetingMessage.class);

        IObject config = IOC.resolve(configObjectKey,
                "{" +
                        "\"in_getName\": \"message/personName\"," +
                        "\"out_setGreeting\": \"response/greeting\"" +
                        "}");
        WDSObject wdsObject = IOC.resolve(wdsObjectKey, config);

        IObject environment = IOC.resolve(iObjectKey,
                "{" +
                        "\"message\": { \"personName\": \"Ivan\" }," +
                        "\"response\": {}" +
                        "}");
        wdsObject.init(environment);

        IObjectWrapper wrapper = (IObjectWrapper) message;
        wrapper.init(wdsObject);

        assertEquals("Ivan", message.getName());

        message.setGreeting("Hello");
        IField responseField = IOC.resolve(iFieldKey, "response");
        IObject response = responseField.in(environment);
        IField greetingField = IOC.resolve(iFieldKey, "greeting");
        assertEquals("Hello", greetingField.in(response));
    }

    @Test
    public void testConcatRule() throws ResolutionException, WrapperGeneratorException, InvalidArgumentException, ReadValueException {
        IWrapperGenerator generator = IOC.resolve(iWrapperGeneratorKey);
        GreetingMessage message = generator.generate(GreetingMessage.class);

        IObject config = IOC.resolve(configObjectKey,
                "{" +
                "\"in_getName\": [" +
                "{" +
                "\"name\": \"concat_strategy\", " +
                "\"args\": [ \"message/firstName\", \"const/ \", \"message/lastName\" ]" +
                "}" +
                "]" +
                "}");
        WDSObject wdsObject = IOC.resolve(wdsObjectKey, config);

        IObject environment = IOC.resolve(iObjectKey,
                "{" +
                "\"message\": { \"firstName\": \"Ivan\", \"lastName\": \"Petrov\" }" +
                "}");
        wdsObject.init(environment);

        IObjectWrapper wrapper = (IObjectWrapper) message;
        wrapper.init(wdsObject);

        assertEquals("Ivan Petrov", message.getName());
    }

    @Test
    public void testSplitRule() throws ResolutionException, WrapperGeneratorException, InvalidArgumentException, ReadValueException, ChangeValueException {
        IObject config = IOC.resolve(configObjectKey,
                "{\"out_setName\": [[" +
                "{" +
                "\"name\": \"split_strategy\"," +
                "\"args\": [ \"local/value\", \"const/ \" ]" +
                "}," +
                "{" +
                "\"name\": \"target\"," +
                "\"args\": [ \"response/namesList\" ]" +
                "}\n" +
                "]]}");
        WDSObject wdsObject = IOC.resolve(wdsObjectKey, config);

        IObject environment = IOC.resolve(iObjectKey,
                "{" +
                "\"response\": { }" +
                "}");
        wdsObject.init(environment);

        IField setNameField = IOC.resolve(iFieldKey, "out_setName");
        setNameField.out(wdsObject, "Ivan Petrov");

        IField responseField = IOC.resolve(iFieldKey, "response");
        IObject response = responseField.in(environment);
        IField namesListField = IOC.resolve(iFieldKey, "namesList");
        List<String> namesList = namesListField.in(response);
        assertEquals("Ivan", namesList.get(0));
        assertEquals("Petrov", namesList.get(1));
    }


}
