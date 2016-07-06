package info.smart_tools.smartactors.core.wrapper_generator;


import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.core.iwrapper_generator.exception.WrapperGeneratorException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WrapperGenerator}
 */
public class WrapperGeneratorTest {

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), new StrategyContainer());
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
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

        IResolveDependencyStrategy toListOfInt = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToListOfInt"), toListOfInt);
        when(toListOfInt.resolve(any())).thenReturn(new ArrayList<Integer>(){{add(1);}});

        IResolveDependencyStrategy toListOfString = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToListOfString"), toListOfString);
        when(toListOfString.resolve(any())).thenReturn(new ArrayList<String>(){{add("abc");}});

        IResolveDependencyStrategy toListOfTestClasses = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToListOfTestClasses"), toListOfTestClasses);
        TestClass testClass = new TestClass();
        testClass.setF(1.5f);
        when(toListOfTestClasses.resolve(any())).thenReturn(new ArrayList<TestClass>(){{add(testClass);}});

        IInnerWrapper innerWrapper = mock(IInnerWrapper.class);
        IResolveDependencyStrategy toMapOfStringIInnerWrapper = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToMapOfStringIInnerWrapper"), toMapOfStringIInnerWrapper);
        when(toMapOfStringIInnerWrapper.resolve(any())).thenReturn(new HashMap<String, IInnerWrapper>(){{put("abc", innerWrapper);}});

        IResolveDependencyStrategy getInnerWrapperFromMapByName = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToInnerWrapperFromMapByName"), getInnerWrapperFromMapByName);
        when(getInnerWrapperFromMapByName.resolve(any(), eq("abc"))).thenReturn(innerWrapper);

        fillBinding();
    }

    private void fillBinding()
            throws Exception {
        IObject binding = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "binding");
        IObject bindingForIWrapper = new DSObject();
        IObject bindingForIInnerWrapper = new DSObject();
        IObject bindingForIIncorrectWrapperWithoutReadValueException = new DSObject();
        IObject bindingForIIncorrectWrapperWithoutChangeValueException = new DSObject();

        // Binding for IIncorrectWrapperWithoutReadValueException methods
        IObject getValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");

        // Binding for IIncorrectWrapperWithoutChangeValueException methods
        IObject setValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/Value\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");

        // Binding for IInnerWrapper methods
        IObject getDoubleValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/DoubleValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setDoubleValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/DoubleValue\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");

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
        IObject getTestClassValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/TestClassValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setTestClassValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/TestClassValue\"\n" +
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
        IObject getListOfTestClasses = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"ToListOfTestClasses\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/ListOfTestClasses\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setListOfTestClasses = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/ListOfTestClasses\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getBoolValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/BoolValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setBoolValue = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/BoolValue\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getWrappedIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/WrappedIObject\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setWrappedIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/WrappedIObject\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/IObject\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject setIObject = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"response/IObject\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");
        IObject getInnerMapByName = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"ToMapOfStringIInnerWrapper\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"context/subcontext/StringIInnerMap\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"context/subcontext/MapOfStringIInnerWrapper\"\n" +
                "\t}, {\n" +
                "\t\t\"name\": \"ToInnerWrapperFromMapByName\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"context/subcontext/MapOfStringIInnerWrapper\",\n" +
                "\t\t\t\"context/subcontext/StringValue\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}]\n" +
                "}");
        IObject setStringIInnerMap = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\"in\"],\n" +
                "\t\t\"target\": \"context/subcontext/StringIInnerMap\"\n" +
                "\t}\n" +
                "\t]\n" +
                "}");

        bindingForIIncorrectWrapperWithoutReadValueException.setValue(new FieldName("getValue"), getValue);

        bindingForIIncorrectWrapperWithoutChangeValueException.setValue(new FieldName("setValue"), setValue);

        bindingForIInnerWrapper.setValue(new FieldName("getDoubleValue"), getDoubleValue);
        bindingForIInnerWrapper.setValue(new FieldName("setDoubleValue"), setDoubleValue);

        bindingForIWrapper.setValue(new FieldName("getIntValue"), getIntValue);
        bindingForIWrapper.setValue(new FieldName("setIntValue"), setIntValue);
        bindingForIWrapper.setValue(new FieldName("getStringValue"), getStringValue);
        bindingForIWrapper.setValue(new FieldName("setStringValue"), setStringValue);
        bindingForIWrapper.setValue(new FieldName("getTestClassValue"), getTestClassValue);
        bindingForIWrapper.setValue(new FieldName("setTestClassValue"), setTestClassValue);
        bindingForIWrapper.setValue(new FieldName("getListOfInt"), getListOfInt);
        bindingForIWrapper.setValue(new FieldName("setListOfInt"), setListOfInt);
        bindingForIWrapper.setValue(new FieldName("getListOfString"), getListOfString);
        bindingForIWrapper.setValue(new FieldName("setListOfString"), setListOfString);
        bindingForIWrapper.setValue(new FieldName("getListOfTestClasses"), getListOfTestClasses);
        bindingForIWrapper.setValue(new FieldName("setListOfTestClasses"), setListOfTestClasses);
        bindingForIWrapper.setValue(new FieldName("getBoolValue"), getBoolValue);
        bindingForIWrapper.setValue(new FieldName("setBoolValue"), setBoolValue);
        bindingForIWrapper.setValue(new FieldName("getWrappedIObject"), getWrappedIObject);
        bindingForIWrapper.setValue(new FieldName("setWrappedIObject"), setWrappedIObject);
        bindingForIWrapper.setValue(new FieldName("getIObject"), getIObject);
        bindingForIWrapper.setValue(new FieldName("setIObject"), setIObject);
        bindingForIWrapper.setValue(new FieldName("getInnerMapByName"), getInnerMapByName);
        bindingForIWrapper.setValue(new FieldName("setStringIInnerMap"), setStringIInnerMap);

        binding.setValue(new FieldName(
                IIncorrectWrapperWithoutReadValueException.class.getCanonicalName()),
                bindingForIIncorrectWrapperWithoutReadValueException
        );
        binding.setValue(new FieldName(
                        IIncorrectWrapperWithoutChangeValueException.class.getCanonicalName()),
                bindingForIIncorrectWrapperWithoutChangeValueException
        );

        binding.setValue(new FieldName(IWrapper.class.getCanonicalName()), bindingForIWrapper);
        binding.setValue(new FieldName(IInnerWrapper.class.getCanonicalName()), bindingForIInnerWrapper);
    }

    @Test
    public void checkCreationAndUsageWrapperByInterface()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);

        IWrapper inst = wg.generate(IWrapper.class);

        assertNotNull(inst);

        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject context = mock(IObject.class);
        IObject subContext = mock(IObject.class);
        IObject response = mock(IObject.class);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(env.getValue(new FieldName("context"))).thenReturn(context);
        when(env.getValue(new FieldName("response"))).thenReturn(response);
        when(context.getValue(new FieldName("subcontext"))).thenReturn(subContext);

        when(message.getValue(new FieldName("IntValue"))).thenReturn(1);
        when(message.getValue(new FieldName("StringValue"))).thenReturn("abc");
        TestClass testClass = new TestClass();
        when(message.getValue(new FieldName("TestClassValue"))).thenReturn(testClass);
        List<Integer> listOfInt = new ArrayList<Integer>(){{add(1);}};
        when(message.getValue(new FieldName("ListOfInt"))).thenReturn(listOfInt);
        List<String> listOfString = new ArrayList<String>(){{add("abc");}};
        when(message.getValue(new FieldName("ListOfString"))).thenReturn(listOfString);
        List<TestClass> listOfTestClasses = new ArrayList<TestClass>(){{new TestClass();}};
        when(message.getValue(new FieldName("ListOfTestClasses"))).thenReturn(listOfTestClasses);
        when(message.getValue(new FieldName("BoolValue"))).thenReturn(true);
        IInnerWrapper innerWrapper = mock(IInnerWrapper.class);
        when(message.getValue(new FieldName("WrappedIObject"))).thenReturn(innerWrapper);
        IObject iObject = mock(IObject.class);
        when(message.getValue(new FieldName("IObject"))).thenReturn(iObject);
        when(subContext.getValue(new FieldName("StringIInnerMap"))).thenReturn(new HashMap<String, IInnerWrapper>(){{put("abc", innerWrapper);}});
        when(subContext.getValue(new FieldName("MapOfStringIInnerWrapper"))).thenReturn(new HashMap<String, IInnerWrapper>(){{put("abc", innerWrapper);}});
        when(subContext.getValue(new FieldName("StringValue"))).thenReturn("abc");

        ((IObjectWrapper)inst).init(env);

        int intResult = inst.getIntValue();
        String stringResult = inst.getStringValue();
        TestClass testClassResult = inst.getTestClassValue();
        List<Integer> integerList = inst.getListOfInt();
        List<String> stringList = inst.getListOfString();
        List<TestClass> testClasses = inst.getListOfTestClasses();
        boolean boolResult = inst.getBoolValue();
        IInnerWrapper innerWrapperResult = inst.getWrappedIObject();
        IObject iObjectResult = inst.getIObject();
        //Map<String, IInnerWrapper> mapResult = inst.getStringIInnerMap();
        IInnerWrapper resultOfChainStrategy = inst.getInnerMapByName();
        IObject resultMessage = ((IObjectWrapper) inst).getEnvironmentIObject(new FieldName("message"));
        IObject resultContext = ((IObjectWrapper) inst).getEnvironmentIObject(new FieldName("context"));
        IObject resultResponse = ((IObjectWrapper) inst).getEnvironmentIObject(new FieldName("response"));

        assertEquals(intResult, 1);
        assertEquals(stringResult, "abc");
        assertEquals(testClassResult, testClass);
        assertEquals((int) integerList.get(0), 1);
        assertEquals(stringList.get(0), "abc");
        assertEquals(testClasses.get(0).getF(), 1.5f, 0);
        assertEquals(boolResult, true);
        assertEquals(innerWrapperResult, innerWrapper);
        assertEquals(iObjectResult, iObject);

        assertNotNull(resultOfChainStrategy);
        assertTrue(IInnerWrapper.class.isAssignableFrom(resultOfChainStrategy.getClass()));

        assertSame(resultMessage, message);
        assertSame(resultContext, context);
        assertSame(resultResponse, response);

        inst.setIntValue(2);
        inst.setStringValue("cba");
        TestClass testClassForSetter = new TestClass();
        inst.setTestClassValue(testClassForSetter);
        List<Integer> integerListForSetter = new ArrayList<Integer>(){{add(2);}};
        inst.setListOfInt(integerListForSetter);
        List<String> stringListForSetter = new ArrayList<String>(){{add("cba");}};
        inst.setListOfString(stringListForSetter);
        List<TestClass> testClassListForSetter = new ArrayList<TestClass>(){{add(testClassForSetter);}};
        inst.setListOfTestClasses(testClassListForSetter);
        inst.setBoolValue(false);
        IInnerWrapper innerWrapperForSetter = mock(IInnerWrapper.class);
        inst.setWrappedIObject(innerWrapperForSetter);
        IObject iObjectForSetter = mock(IObject.class);
        inst.setIObject(iObjectForSetter);
        inst.setStringIInnerMap(new HashMap<String, IInnerWrapper>(){{put("cba", innerWrapperForSetter);}});

        verify(response, times(1)).setValue(new FieldName("IntValue"), 2);
        verify(response, times(1)).setValue(new FieldName("StringValue"), "cba");
        verify(response, times(1)).setValue(new FieldName("TestClassValue"), testClassForSetter);
        verify(response, times(1)).setValue(new FieldName("ListOfInt"), integerListForSetter);
        verify(response, times(1)).setValue(new FieldName("ListOfString"), stringListForSetter);
        verify(response, times(1)).setValue(new FieldName("ListOfTestClasses"), testClassListForSetter);
        verify(response, times(1)).setValue(new FieldName("BoolValue"), false);
        verify(response, times(1)).setValue(new FieldName("WrappedIObject"), innerWrapperForSetter);
        verify(response, times(1)).setValue(new FieldName("IObject"), iObjectForSetter);
        verify(subContext, times(1)).setValue(new FieldName("StringIInnerMap"), new HashMap<String, IInnerWrapper>(){{put("cba", innerWrapperForSetter);}});
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutReadValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(IIncorrectWrapperWithoutReadValueException.class);
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutChangeValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(IIncorrectWrapperWithoutChangeValueException.class);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTargetInterfaceNull()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNotInterface()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(TestClass.class);
        fail();
    }
}

