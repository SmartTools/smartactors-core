package info.smart_tools.smartactors.core.wds_object;

import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WDSObjectField}
 */
public class WDSObjectFieldTest {

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
        IOC.register(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> a[1]
                )
        );
    }


    @Test
    public void checkFieldCreationAndSimpleGetter()
            throws Exception {
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject submessage = mock(IObject.class);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("submessage"))).thenReturn(submessage);
        when(submessage.getValue(new FieldName("intValue"))).thenReturn("1");
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_getter_strategy");
        when(rule.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("message/submessage/intValue");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        assertNotNull(field);
        assertEquals(field.in(env), "1");
    }

    @Test (expected = ReadValueException.class)
    public void checkSimpleGetterWithException()
            throws Exception {
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject submessage = mock(IObject.class);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("submessage"))).thenReturn(submessage);
        when(submessage.getValue(new FieldName("intValue"))).thenReturn(1);
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_getter_strategy");
        when(rule.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("message/undefined/submessage/intValue");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        // value of 'undefined' is undefined (not mocked)
        field.in(env);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnCreationWithNullArgument()
            throws Exception {
        IField field = new WDSObjectField(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnCreationWithWrongArgument()
            throws Exception {
        IObject rule = mock(IObject.class);
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        fail();
    }

    @Test
    public void checkInMethod()
            throws Exception {
        IResolveDependencyStrategy strategy1 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IResolveDependencyStrategy strategy2 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "Transform",
                strategy2
        );
        when(strategy1.resolve("CONST", 1)).thenReturn("CONST1");
        when(strategy2.resolve("CONST1", "abc")).thenReturn("CONST1abc");
        IObject rule1 = mock(IObject.class);
        IObject rule2 = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        when(rule1.getValue(new FieldName("name"))).thenReturn("GetValue");
        when(rule1.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("consts/CONST"); add("message/intValue");}});
        when(rule2.getValue(new FieldName("name"))).thenReturn("Transform");
        when(rule2.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("consts/abc");}});
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("intValue"))).thenReturn(1);
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule1);add(rule2);}});
        assertEquals(field.in(env), "CONST1abc");
        when(strategy2.resolve("CONST1", "abc")).thenReturn(null);
        assertNull(field.in(env));
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodExceptionOnNullArgs()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_getter_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.in(null);
        fail();
    }

    @Test (expected = ReadValueException.class)
    public void checkInMethodExceptionOnWrongArgs()
            throws Exception {
        IResolveDependencyStrategy strategy1 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        when(rule.getValue(new FieldName("name"))).thenReturn("GetValue");
        when(rule.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("message/getInt");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        // value of 'message/getInt' is undefined
        field.in(env);
        fail();
    }

    @Test
    public void checkOutMethod()
            throws Exception {
        IResolveDependencyStrategy strategy1 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IResolveDependencyStrategy strategy2 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "Transform",
                strategy2
        );
        when(strategy1.resolve(2, 1)).thenReturn(3);
        when(strategy2.resolve("CONST", 3)).thenReturn("CONST3");
        IObject rule1 = mock(IObject.class);
        IObject rule2 = mock(IObject.class);
        IObject rule3 = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject response = mock(IObject.class);
        when(rule1.getValue(new FieldName("name"))).thenReturn("GetValue");
        when(rule1.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("message/intValue");}});
        when(rule2.getValue(new FieldName("name"))).thenReturn("Transform");
        when(rule2.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("consts/CONST"); add("local/value");}});
        when(rule3.getValue(new FieldName("name"))).thenReturn("wds_target_strategy");
        when(rule3.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("response/intValue");}});
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(env.getValue(new FieldName("response"))).thenReturn(response);
        when(message.getValue(new FieldName("intValue"))).thenReturn(1);
        doNothing().when(response).setValue(new FieldName("intValue"), "CONST3");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule1);add(rule2);add(rule3);}});
        field.out(env, 2);
        verify(response, times(1)).setValue(new FieldName("intValue"), "CONST3");
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutMethodExceptionOnNullArgs()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_target_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.out(null, 1);
        fail();
    }

    @Test (expected = ChangeValueException.class)
    public void checkOutMethodExceptionOnWrongArgs()
            throws Exception {
        IResolveDependencyStrategy strategy1 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_target_strategy");
        when(rule.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("response/getInt");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        // value of 'message/getInt' is undefined
        field.out(env, 1);
        fail();
    }

    @Test (expected = ReadValueException.class)
    public void checkNotImplementedInMethodException()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_getter_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.in(null, Object.class);
        fail();
    }
}
