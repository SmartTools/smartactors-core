package info.smart_tools.smartactors.core.server_with_iobject;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
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
import info.smart_tools.smartactors.core.wds_object.WDSObject;
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
        } catch (Throwable e) {
            throw new ServerInitializeException("Could not initialize server.");
        }
    }

    @Override
    public void start()
            throws ServerExecutionException {
        try {
            /** Get wrapper generator by IOC.resolve */
            IWrapperGenerator wg = IOC.resolve(Keys.getOrAdd(IWrapperGenerator.class.getCanonicalName()));

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
            IWrapper newInstanceOfWrapper = IOC.resolve(Keys.getOrAdd(IWrapper.class.getCanonicalName() + "wrapper"));

            WDSObject wds = new WDSObject(((IObject) getWDSConfig().getValue(new FieldName("wrapper"))));

            /** Initialize wrapper */
            wds.init(environment);
            ((IObjectWrapper) wrapper).init(wds);

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
                Keys.getOrAdd(FieldName.class.getCanonicalName()),
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
        IOC.register(Keys.getOrAdd(IWrapperGenerator.class.getCanonicalName()), new SingletonStrategy(wg));
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

    private IObject getWDSConfig()
            throws Exception {
        IObject config = new DSObject("{\n" +
                "  \"wrapper\": {\n" +
                "    \"in_getIntValue\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"message/IntValue\"]\n" +
                "    }],\n" +
                "    \"out_setIntValue\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/IntValue\"]\n" +
                "      }]\n" +
                "    ],\n" +
                "    \"in_getStringValue\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"message/StringValue\"]\n" +
                "    }],\n" +
                "    \"out_setStringValue\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/StringValue\"]\n" +
                "      }]\n" +
                "    ],\n" +
                "    \"in_getListOfInt\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"message/ListOfInt\"]\n" +
                "    }],\n" +
                "    \"out_setListOfInt\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/ListOfInt\"]\n" +
                "      }]\n" +
                "    ],\n" +
                "    \"in_getListOfString\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"message/ListOfString\"]\n" +
                "    }],\n" +
                "    \"out_setListOfString\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/ListOfString\"]\n" +
                "      }]\n" +
                "    ],\n" +
                "    \"in_getBoolValue\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"context/BoolValue\"]\n" +
                "    }],\n" +
                "    \"out_setBoolValue\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/BoolValue\"]\n" +
                "      }]\n" +
                "    ],\n" +
                "    \"in_getIObject\": [{\n" +
                "      \"name\": \"wds_getter_strategy\",\n" +
                "      \"args\": [\"context/IObject\"]\n" +
                "    }],\n" +
                "    \"out_setIObject\": [\n" +
                "      [{\n" +
                "        \"name\": \"wds_target_strategy\",\n" +
                "        \"args\": [\"local/value\", \"response/IObject\"]\n" +
                "      }]\n" +
                "    ]\n" +
                "  }\n" +
                "}");
//
//        List<IObject> list_in_getIntValue= new ArrayList<>();
//        IObject in_getIntValue = new DSObject("{\n" +
//                "      \"name\": \"wds_getter_strategy\",\n" +
//                "      \"args\": [\"message/IntValue\"]\n" +
//                "    }");
//        list_in_getIntValue.add(in_getIntValue);
//        config.setValue(new FieldName("in_getIntValue"), list_in_getIntValue);
//
//        ArrayList<IObject> inner_list_out_setIntValue = new ArrayList<>();
//        List<List<IObject>> list_out_setIntValue = new ArrayList<List<IObject>>();
//        list_out_setIntValue.add(inner_list_out_setIntValue);
//        IObject out_setIntValue = new DSObject("{\n" +
//                "        \"name\": \"wds_target_strategy\",\n" +
//                "        \"args\": [\"local/value\", \"response/IntValue\"]\n" +
//                "      }");
//        inner_list_out_setIntValue.add(out_setIntValue);
//        config.setValue(new FieldName("out_setIntValue"), list_out_setIntValue);
//
//        List<IObject> list_in_getStringValue = new ArrayList<>();
//        IObject in_getStringValue = new DSObject("{\n" +
//                "      \"name\": \"wds_getter_strategy\",\n" +
//                "      \"args\": [\"message/StringValue\"]\n" +
//                "    }");
//        list_in_getStringValue.add(in_getStringValue);
//        config.setValue(new FieldName("in_getStringValue"), list_in_getStringValue);
//
//        ArrayList<IObject> inner_list_out_setStringValue = new ArrayList<>();
//        List<List<IObject>> list_out_setStringValue = new ArrayList<List<IObject>>();
//        list_out_setStringValue.add(inner_list_out_setStringValue);
//        IObject out_setStringValue = new DSObject("{\n" +
//                "        \"name\": \"wds_target_strategy\",\n" +
//                "        \"args\": [\"local/value\", \"response/StringValue\"]\n" +
//                "      }");
//        inner_list_out_setStringValue.add(out_setStringValue);
//        config.setValue(new FieldName("out_setStringValue"), list_out_setStringValue);
//
//        List<IObject> list_in_getListOfInt = new ArrayList<>();
//        IObject in_getListOfInt = new DSObject("{\n" +
//                "      \"name\": \"wds_getter_strategy\",\n" +
//                "      \"args\": [\"message/ListOfInt\"]\n" +
//                "    }");
//        list_in_getListOfInt.add(in_getListOfInt);
//        config.setValue(new FieldName("in_getListOfInt"), list_in_getListOfInt);
//
//        ArrayList<IObject> inner_list_out_setListOfInt = new ArrayList<>();
//        List<List<IObject>> list_out_setListOfInt = new ArrayList<List<IObject>>();
//        list_out_setListOfInt.add(inner_list_out_setListOfInt);
//        IObject out_setListOfInt = new DSObject("{\n" +
//                "        \"name\": \"wds_target_strategy\",\n" +
//                "        \"args\": [\"local/value\", \"response/ListOfInt\"]\n" +
//                "      }");
//        inner_list_out_setListOfInt.add(out_setListOfInt);
//        config.setValue(new FieldName("out_setListOfInt"), list_out_setListOfInt);
//
//        List<IObject> list_in_getListOfString = new ArrayList<>();
//        IObject in_getListOfString = new DSObject();
//        list_in_getListOfString.add(in_getListOfString);
//        config.setValue(new FieldName("in_getListOfString"), list_in_getListOfString);
//
//        ArrayList<IObject> inner_list_out_setListOfString = new ArrayList<>();
//        List<List<IObject>> list_out_setListOfString = new ArrayList<List<IObject>>();
//        list_out_setListOfString.add(inner_list_out_setListOfString);
//        IObject out_setListOfString = new DSObject("");
//        inner_list_out_.add(out_);
//        config.setValue(new FieldName("out_"), list_out_);
//
//        List<IObject> list_in_ = new ArrayList<>();
//        IObject in_ = new DSObject();
//        list_in_.add(in_);
//        config.setValue(new FieldName("in_"), list_in_);
//
//        ArrayList<IObject> inner_list_out_ = new ArrayList<>();
//        List<List<IObject>> list_out_ = new ArrayList<List<IObject>>();
//        list_out_.add(inner_list_out_);
//        IObject out_ = new DSObject("");
//        inner_list_out_.add(out_);
//        config.setValue(new FieldName("out_"), list_out_);
//
//
//        List<IObject> list_in_ = new ArrayList<>();
//        IObject in_ = new DSObject();
//        list_in_.add(in_);
//        config.setValue(new FieldName("in_"), list_in_);
//
//        ArrayList<IObject> inner_list_out_ = new ArrayList<>();
//        List<List<IObject>> list_out_ = new ArrayList<List<IObject>>();
//        list_out_.add(inner_list_out_);
//        IObject out_ = new DSObject("");
//        inner_list_out_.add(out_);
//        config.setValue(new FieldName("out_"), list_out_);

        return config;
    }
}