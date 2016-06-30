package info.smart_tools.smartactors.core.wrapper_generator;


import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
    }

    private IObject getBinding()
            throws Exception {
        IObject binding = new DSObject();
        IObject bindingForIWrapper = new DSObject();
        IObject bindingForIInnerWrapper = new DSObject();
        IObject bindingForIIncorrectWrapperWithoutReadValueException = new DSObject();
        IObject bindingForIIncorrectWrapperWithoutChangeValueException = new DSObject();

        IObject getValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"Value\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");

        IObject setValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"Value\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");

        IObject getDoubleValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"DoubleValue\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setDoubleValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"DoubleValue\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");

        IObject getIntValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"IntValue\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setIntValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"IntValue\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getStringValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"StringValue\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setStringValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"StringValue\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getTestClassValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"TestClassValue\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setTestClassValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"TestClassValue\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getListOfInt = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfInt\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"ToListOfInt\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setListOfInt = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfInt\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getListOfString = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfString\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"ToListOfString\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setListOfString = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfString\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getListOfTestClasses = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfTestClasses\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"ToListOfTestClasses\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setListOfTestClasses = new DSObject("{\n" +
                "\t\"ValueName\":    \"ListOfTestClasses\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getBoolValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"BoolValue\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setBoolValue = new DSObject("{\n" +
                "\t\"ValueName\":    \"BoolValue\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getWrappedIObject = new DSObject("{\n" +
                "\t\"ValueName\":    \"WrappedIObject\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": true\n" +
                "}");
        IObject setWrappedIObject = new DSObject("{\n" +
                "\t\"ValueName\":    \"WrappedIObject\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": true\n" +
                "}");
        IObject getIObject = new DSObject("{\n" +
                "\t\"ValueName\":    \"IObject\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"message\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setIObject = new DSObject("{\n" +
                "\t\"ValueName\":    \"IObject\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"response\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject getStringIInnerMap = new DSObject("{\n" +
                "\t\"ValueName\":    \"StringIInnerMap\",\n" +
                "\t\"MethodType\":   \"get\",\n" +
                "\t\"Resource\":     \"context\",\n" +
                "\t\"UseStrategy\":  \"ToMapOfStringIInnerWrapper\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");
        IObject setStringIInnerMap = new DSObject("{\n" +
                "\t\"ValueName\":    \"StringIInnerMap\",\n" +
                "\t\"MethodType\":   \"set\",\n" +
                "\t\"Resource\":     \"context\",\n" +
                "\t\"UseStrategy\":  \"\",\n" +
                "\t\"CheckWrapper\": false\n" +
                "}");

        bindingForIIncorrectWrapperWithoutReadValueException.setValue(new FieldName("getValue"), getValue);
        bindingForIIncorrectWrapperWithoutReadValueException.setValue(new FieldName("initMethodParameters"), new String[]{"message"});

        bindingForIIncorrectWrapperWithoutChangeValueException.setValue(new FieldName("setValue"), setValue);
        bindingForIIncorrectWrapperWithoutChangeValueException.setValue(new FieldName("initMethodParameters"), new String[]{"message"});

        bindingForIInnerWrapper.setValue(new FieldName("getDoubleValue"), getDoubleValue);
        bindingForIInnerWrapper.setValue(new FieldName("setDoubleValue"), setDoubleValue);
        bindingForIInnerWrapper.setValue(new FieldName("initMethodParameters"), new String[]{"message", "response"});

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
        bindingForIWrapper.setValue(new FieldName("getStringIInnerMap"), getStringIInnerMap);
        bindingForIWrapper.setValue(new FieldName("setStringIInnerMap"), setStringIInnerMap);
        bindingForIWrapper.setValue(new FieldName("initMethodParameters"), new String[]{"message", "context", "response"});

        binding.setValue(new FieldName(
                IIncorrectWrapperWithoutReadValueException.class.toString()),
                bindingForIIncorrectWrapperWithoutReadValueException
        );
        binding.setValue(new FieldName(
                        IIncorrectWrapperWithoutChangeValueException.class.toString()),
                bindingForIIncorrectWrapperWithoutChangeValueException
        );

        binding.setValue(new FieldName(IWrapper.class.toString()), bindingForIWrapper);
        binding.setValue(new FieldName(IInnerWrapper.class.toString()), bindingForIInnerWrapper);

        return binding;
    }

    @Test
    public void checkCreationAndUsageWrapperByInterface()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        IObject binding = getBinding();

        IWrapper inst = wg.generate(IWrapper.class, binding);

        assertNotNull(inst);

        IObject message = mock(IObject.class);
        IObject context = mock(IObject.class);
        IObject response = mock(IObject.class);

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
        when(context.getValue(new FieldName("StringIInnerMap"))).thenReturn(new HashMap<String, IInnerWrapper>(){{put("abc", innerWrapper);}});

        ((IObjectWrapper)inst).init(message, context, response);

        int intResult = inst.getIntValue();
        String stringResult = inst.getStringValue();
        TestClass testClassResult = inst.getTestClassValue();
        List<Integer> integerList = inst.getListOfInt();
        List<String> stringList = inst.getListOfString();
        List<TestClass> testClasses = inst.getListOfTestClasses();
        boolean boolResult = inst.getBoolValue();
        IInnerWrapper innerWrapperResult = inst.getWrappedIObject();
        IObject iObjectResult = inst.getIObject();
        Map<String, IInnerWrapper> mapResult = inst.getStringIInnerMap();
        IObject[] iObjects = ((IObjectWrapper) inst).getIObjects();

        assertEquals(intResult, 1);
        assertEquals(stringResult, "abc");
        assertEquals(testClassResult, testClass);
        assertEquals((int) integerList.get(0), 1);
        assertEquals(stringList.get(0), "abc");
        assertEquals(testClasses.get(0).getF(), 1.5f, 0);
        assertEquals(boolResult, true);
        assertEquals(innerWrapperResult, innerWrapper);
        assertEquals(iObjectResult, iObject);
        assertNotNull(mapResult.get("abc"));
        assertSame(iObjects[0], message);
        assertSame(iObjects[1], context);
        assertSame(iObjects[2], response);

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
        verify(context, times(1)).setValue(new FieldName("StringIInnerMap"), new HashMap<String, IInnerWrapper>(){{put("cba", innerWrapperForSetter);}});
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutReadValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        IObject binding = getBinding();
        wg.generate(IIncorrectWrapperWithoutReadValueException.class, binding);
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutChangeValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        IObject binding = getBinding();
        wg.generate(IIncorrectWrapperWithoutChangeValueException.class, binding);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTargetInterfaceNull()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(null, null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNotInterface()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(TestClass.class, null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnBindingNull()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        wg.generate(IWrapper.class, null);
        fail();
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkWrapperGeneratorExceptionOnWrongBinding()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator(null);
        IObject binding = mock(IObject.class);
        wg.generate(IWrapper.class, binding);
        fail();
    }
}

