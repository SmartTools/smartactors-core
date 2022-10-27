package info.smart_tools.smartactors.iobject_extension.wds_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.field.field_name_tools.FieldNames;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link WDSObjectField}
 */
public class WDSObjectFieldTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Before
    public void init()
            throws Exception {
        IOC.register(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
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
        when(env.getValue(FieldNames.getFieldNameByName("message"))).thenReturn(message);
        when(message.getValue(FieldNames.getFieldNameByName("submessage"))).thenReturn(submessage);
        when(submessage.getValue(FieldNames.getFieldNameByName("intValue"))).thenReturn("1");
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_getter_strategy");
        when(rule.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("message/submessage/intValue");}});
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
        when(env.getValue(FieldNames.getFieldNameByName("message"))).thenReturn(message);
        when(message.getValue(FieldNames.getFieldNameByName("submessage"))).thenReturn(submessage);
        when(submessage.getValue(FieldNames.getFieldNameByName("intValue"))).thenReturn(1);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_getter_strategy");
        when(rule.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("message/undefined/submessage/intValue");}});
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
        IStrategy strategy1 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IStrategy strategy2 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                "Transform",
                strategy2
        );
        when(strategy1.resolve("CONST/a", 1)).thenReturn("CONST/a1");
        when(strategy2.resolve("CONST/a1", "abc")).thenReturn("CONST/a1abc");
        IObject rule1 = mock(IObject.class);
        IObject rule2 = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        when(rule1.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("GetValue");
        when(rule1.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("const/CONST/a"); add("message/intValue");}});
        when(rule2.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("Transform");
        when(rule2.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("const/abc");}});
        when(env.getValue(FieldNames.getFieldNameByName("message"))).thenReturn(message);
        when(message.getValue(FieldNames.getFieldNameByName("intValue"))).thenReturn(1);
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule1);add(rule2);}});
        assertEquals(field.in(env), "CONST/a1abc");
        when(strategy2.resolve("CONST/a1", "abc")).thenReturn(null);
        assertNull(field.in(env));
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodExceptionOnNullArgs()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_getter_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.in(null);
        fail();
    }

    @Test (expected = ReadValueException.class)
    public void checkInMethodExceptionOnWrongArgs()
            throws Exception {
        IStrategy strategy1 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("GetValue");
        when(rule.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("message/getInt");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        // value of 'message/getInt' is undefined
        field.in(env);
        fail();
    }

    @Test
    public void checkOutMethod()
            throws Exception {
        IStrategy strategy1 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IStrategy strategy2 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
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
        when(rule1.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("GetValue");
        when(rule1.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("message/intValue");}});
        when(rule2.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("Transform");
        when(rule2.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("const/CONST"); add("local/value");}});
        when(rule3.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_target_strategy");
        when(rule3.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("response/intValue");}});
        when(env.getValue(FieldNames.getFieldNameByName("message"))).thenReturn(message);
        when(env.getValue(FieldNames.getFieldNameByName("response"))).thenReturn(response);
        when(message.getValue(FieldNames.getFieldNameByName("intValue"))).thenReturn(1);
        doNothing().when(response).setValue(FieldNames.getFieldNameByName("intValue"), "CONST3");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule1);add(rule2);add(rule3);}});
        field.out(env, 2);
        verify(response, times(1)).setValue(FieldNames.getFieldNameByName("intValue"), "CONST3");
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutMethodExceptionOnNullArgs()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_target_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.out(null, 1);
        fail();
    }

    @Test (expected = ChangeValueException.class)
    public void checkOutMethodExceptionOnWrongArgs()
            throws Exception {
        IStrategy strategy1 = mock(IStrategy.class);
        IOC.resolve(
                Keys.getKeyByName(IStrategy.class.getCanonicalName()),
                "GetValue",
                strategy1
        );
        IObject rule = mock(IObject.class);
        IObject env = mock(IObject.class);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_target_strategy");
        when(rule.getValue(FieldNames.getFieldNameByName("args"))).thenReturn(new ArrayList<String>(){{add("response/getInt");}});
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        // value of 'message/getInt' is undefined
        field.out(env, 1);
        fail();
    }

    @Test (expected = ReadValueException.class)
    public void checkNotImplementedInMethodException()
            throws Exception {
        IObject rule = mock(IObject.class);
        when(rule.getValue(FieldNames.getFieldNameByName("name"))).thenReturn("wds_getter_strategy");
        IField field = new WDSObjectField(new ArrayList<IObject>(){{add(rule);}});
        field.in(null, Object.class);
        fail();
    }
}
