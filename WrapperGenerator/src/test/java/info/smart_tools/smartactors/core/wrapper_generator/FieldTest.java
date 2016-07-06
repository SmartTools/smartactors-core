package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for Field
 */
public class FieldTest {

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
        IResolveDependencyStrategy toInteger = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd(int.class.toString()), toInteger);
        when(toInteger.resolve(any())).thenReturn(1);

        IResolveDependencyStrategy toBoolean = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ToBoolean"), toBoolean);
        when(toBoolean.resolve(any())).thenReturn(true);
    }

    @Test
    public void checkFieldCreation()
            throws Exception {
        Field field = new Field<>("binding");
        assertNotNull(field);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutMethodOnWrongArgument()
            throws Exception {
        Field<Integer> field = new Field<>("binding");
        field.out(null);
        fail();
    }

    @Test
    public void checkOutMethodNullValue()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        Field<Integer> field = new Field<>("binding/Binding");
        Integer result = field.out(env);
        assertNull(result);
    }

    @Test (expected = ReadValueException.class)
    public void checkOutMethodOnWrongBinding()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}\n" +
                "\t],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        Field<Integer> field = new Field<>("wrong/Wrong");
        field.out(env);
        fail();
    }

    @Test
    public void checkOutMethodOnNestedIObjectAndMultipleRules()
            throws Exception {
        Integer value = 1;
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject subMessage = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"message/submessage/Value\"\n" +
                "\t}, {\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/submessage/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("submessage"))).thenReturn(subMessage);
        when(message.getValue(new FieldName("Value"))).thenReturn(value);
        when(subMessage.getValue(new FieldName("Value"))).thenReturn(value);
        Field<Integer> field = new Field<>("binding/Binding");
        Integer result = field.out(env);
        assertEquals(result, value);
    }

    @Test (expected = ClassCastException.class)
    public void checkOutMethodOnWrongTypeCast()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"message/Value\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"out\"\n" +
                "\t}" +
                "\t],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("Value"))).thenReturn(true);
        Field<Integer> field = new Field<>("binding/Binding");
        Integer a = field.out(env);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodOnWrongArgument()
            throws Exception {
        Field<Integer> field = new Field<>("binding");
        field.in(null, 1);
        fail();
    }

    @Test (expected = ChangeValueException.class)
    public void checkInMethodOnWrongBinding()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"in\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"message/wrong.path/Value\"\n" +
                "\t}\n" +
                "\t],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        Field<Integer> field = new Field<>("binding/Binding");
        field.in(env, 1);
        fail();
    }

    @Test
    public void checkInMethodOnCorrectData()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject binding = mock(IObject.class);
        IObject rules = new DSObject("{\n" +
                "\t\"rules\": [{\n" +
                "\t\t\"name\": \"\",\n" +
                "\t\t\"args\": [\n" +
                "\t\t\t\"in\"\n" +
                "\t\t],\n" +
                "\t\t\"target\": \"message/Value\"\n" +
                "\t}\n" +
                "\t],\n" +
                "\t\"isWrapper\": false\n" +
                "}");
        when(env.getValue(new FieldName("binding"))).thenReturn(binding);
        when(binding.getValue(new FieldName("Binding"))).thenReturn(rules);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        Field<Integer> field = new Field<>("binding/Binding");
        field.in(env, 1);
    }
}
