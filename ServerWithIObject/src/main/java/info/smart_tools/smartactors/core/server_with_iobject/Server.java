package info.smart_tools.smartactors.core.server_with_iobject;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import info.smart_tools.smartactors.core.wrapper_generator.WrapperGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IServer} with
 */
public class Server implements IServer {

    @Override
    public void initialize() throws ServerInitializeException {
        try {
            scopeInit();
            registerKeysStorageStrategyAndFieldNameStrategy();
            registerWrapperGenerator();
            registerConverterStrategies();
        } catch (Throwable e) {
            throw new ServerInitializeException("Could not initialize server.");
        }
    }

    @Override
    public void start()
            throws ServerExecutionException {
        try {
            /** Get wrapper generator by IOC.resolve */
            IWrapperGenerator wg = IOC.resolve(Keys.getOrAdd(IWrapperGenerator.class.toString()));

            /** Get message, context, response */
            IObject message = getMessage();
            IObject context = getContext();
            IObject response = getResponse();
            IObject environment = new DSObject();
            environment.setValue(new FieldName("message"), message);
            environment.setValue(new FieldName("context"), context);
            environment.setValue(new FieldName("response"), response);

            /** Generate wrapper class by given interface and create instance of generated class */
            IWrapper wrapper = wg.generate(IWrapper.class);

            /** Check registration of IWrapper instance creation strategy to IOC */
            IWrapper newInstanceOfWrapper = IOC.resolve(Keys.getOrAdd(IWrapper.class.getCanonicalName()));

            /** Initialize wrapper */
            ((IObjectWrapper) wrapper).init(environment);

            /** Wrapper usage: get values */
            Integer i = wrapper.getIntValue();
            String s = wrapper.getStringValue();
            Boolean b = wrapper.getBoolValue();
            IObject io = wrapper.getIObject();
            List<Integer> intList = wrapper.getListOfInt();
            List<String> stringList = wrapper.getListOfString();

            /** Wrapper usage: set values */
            wrapper.setIntValue(2);
            wrapper.setBoolValue(false);
            wrapper.setStringValue("new text");
            wrapper.setIObject(new DSObject());
            wrapper.setListOfString(new ArrayList<String>() {{ add("new string"); }});
            wrapper.setListOfInt(new ArrayList<Integer>() {{ add(2); }});

        } catch (Throwable e) {
            throw new ServerExecutionException(e);
        }
    }

    private void scopeInit()
            throws Exception {
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );


        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);
    }

    private void registerKeysStorageStrategyAndFieldNameStrategy()
            throws Exception {
        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy(
                        (arg) -> {
                            try {
                                return new Key((String) arg[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd(IFieldName.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
    }

    private void registerWrapperGenerator()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        IOC.register(Keys.getOrAdd(IWrapperGenerator.class.toString()), new SingletonStrategy(wg));
    }

    private void registerConverterStrategies()
            throws Exception {
        IOC.register(Keys.getOrAdd("ToListOfInt"),
                new CreateNewInstanceStrategy(
                        (arg) -> {
                            try {
                                return arg[0];
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(Keys.getOrAdd("ToListOfString"),
                new CreateNewInstanceStrategy(
                        (arg) -> {
                            try {
                                return arg[0];
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd(IObject.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new DSObject();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        fillBinding();
    }

    private void fillBinding()
            throws Exception {
        IObject binding = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "binding");
        IObject bindingForIWrapper = new DSObject();

        // Binding for IWrapper methods
        IObject getIntValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/IntValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setIntValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/IntValue\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getStringValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/StringValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setStringValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/StringValue\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getListOfInt = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"ToListOfInt\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/ListOfInt\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setListOfInt = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/ListOfInt\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getListOfString = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"ToListOfString\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/ListOfString\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setListOfString = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/ListOfString\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getBoolValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"context/BoolValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setBoolValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"context/BoolValue\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"context/IObject\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"context/IObject\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");

        bindingForIWrapper.setValue(new FieldName("getIntValue"), getIntValue);
        bindingForIWrapper.setValue(new FieldName("setIntValue"), setIntValue);
        bindingForIWrapper.setValue(new FieldName("getStringValue"), getStringValue);
        bindingForIWrapper.setValue(new FieldName("setStringValue"), setStringValue);
        bindingForIWrapper.setValue(new FieldName("getListOfInt"), getListOfInt);
        bindingForIWrapper.setValue(new FieldName("setListOfInt"), setListOfInt);
        bindingForIWrapper.setValue(new FieldName("getListOfString"), getListOfString);
        bindingForIWrapper.setValue(new FieldName("setListOfString"), setListOfString);
        bindingForIWrapper.setValue(new FieldName("getBoolValue"), getBoolValue);
        bindingForIWrapper.setValue(new FieldName("setBoolValue"), setBoolValue);
        bindingForIWrapper.setValue(new FieldName("getIObject"), getIObject);
        bindingForIWrapper.setValue(new FieldName("setIObject"), setIObject);

        binding.setValue(new FieldName(IWrapper.class.getCanonicalName()), bindingForIWrapper);
    }

    private IObject getMessage()
            throws Exception {
        IObject obj = new DSObject();
        obj.setValue(new FieldName("IntValue"), 1);
        obj.setValue(new FieldName("StringValue"), "some text");
        obj.setValue(
                new FieldName("ListOfInt"),
                new ArrayList<Integer>() {{ add(1); add(2); }}
        );
        obj.setValue(
                new FieldName("ListOfString"),
                new ArrayList<String>() {{ add("some text"); add("another text"); }}
        );

        return obj;
    }

    private IObject getContext()
            throws Exception {
        IObject obj = new DSObject();
        obj.setValue(new FieldName("BoolValue"), true);
        obj.setValue(new FieldName("IObject"), new DSObject());

        return obj;
    }

    private IObject getResponse()
            throws Exception {
        IObject obj = new DSObject();

        return obj;
    }

}