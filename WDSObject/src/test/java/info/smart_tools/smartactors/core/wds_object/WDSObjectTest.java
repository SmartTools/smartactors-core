package info.smart_tools.smartactors.core.wds_object;

import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
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
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WDSObject}
 */
public class WDSObjectTest {

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
    public void checkCreation()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject wObj = new WDSObject(env);
        assertNotNull(wObj);
    }

    @Test
    public void checkIObjectWrapperInterfaceMethods()
            throws Exception {
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        IObject wObj = new WDSObject(null);
        ((IObjectWrapper)wObj).init(env);
        IObject result = ((IObjectWrapper)wObj).getEnvironmentIObject(new FieldName("message"));
        assertSame(result, message);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkGetEnvironmentIObjectMehtodExceptionOnNullArgument()
            throws Exception {
        IObject wObj = new WDSObject(null);
        ((IObjectWrapper) wObj).getEnvironmentIObject(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkGetEnvironmentIObjectMehtodExceptionOnWrongArgument()
            throws Exception {
        IObject wObj = new WDSObject(null);
        ((IObjectWrapper)wObj).getEnvironmentIObject(new FieldName("message"));
        fail();
    }

    @Test (expected = DeleteValueException.class)
    public void checkExceptionOnUseDeleteMethod()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.deleteField(new FieldName("field"));
        fail();
    }

    @Test (expected = SerializeException.class)
    public void checkExceptionOnUseSerializeMethod()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.serialize();
        fail();
    }

    @Test
    public void checkNullOnUseGetIterator()
            throws Exception {
        IObject wObj = new WDSObject(null);
        Iterator it = wObj.iterator();
        assertNull(it);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkGetValueMethodOnNullFieldName()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.getValue(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkSetValueMethodOnNullFieldName()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.setValue(null, 1);
        fail();
    }

    @Test (expected = ReadValueException.class)
    public void checkGetValueMethodOnWrongArgument()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.getValue(new FieldName("undefinedFieldName"));
        fail();
    }

    @Test (expected = ChangeValueException.class)
    public void checkSetValueMethodOnWrongArgument()
            throws Exception {
        IObject wObj = new WDSObject(null);
        wObj.setValue(new FieldName("undefinedFieldName"), 1);
        fail();
    }

    @Test
    public void checkGetValueMethod()
            throws Exception {
        IObject config = mock(IObject.class);
        IObject rule = mock(IObject.class);
        List<IObject> rulesList = new ArrayList<IObject>(){{add(rule);}};
        when(config.getValue(new FieldName("StringValue"))).thenReturn(rulesList);
        IObject env = mock(IObject.class);
        IObject message = mock(IObject.class);
        IObject submessage = mock(IObject.class);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("submessage"))).thenReturn(submessage);
        when(submessage.getValue(new FieldName("stringValue"))).thenReturn("1");
        when(rule.getValue(new FieldName("name"))).thenReturn("wds_getter_strategy");
        when(rule.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("message/submessage/stringValue");}});
        IObject wObj = new WDSObject(config);
        ((IObjectWrapper) wObj).init(env);
        String i = (String) wObj.getValue(new FieldName("StringValue"));
        assertEquals(i, "1");
    }

    @Test
    public void checkSetValueMethod()
            throws Exception {
        IResolveDependencyStrategy strategy1 = mock(IResolveDependencyStrategy.class);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                "TransformToInt",
                strategy1
        );
        when(strategy1.resolve("2", "1")).thenReturn(21);
        IObject config = mock(IObject.class);
        IObject rule11 = mock(IObject.class);
        IObject rule21 = mock(IObject.class);
        IObject rule22 = mock(IObject.class);
        List<IObject> rulesList1 = new ArrayList<IObject>(){{add(rule11);}};
        List<IObject> rulesList2 = new ArrayList<IObject>(){{add(rule21); add(rule22);}};
        List<List<IObject>> rules = new ArrayList<List<IObject>>(){{add(rulesList1);  add(rulesList2);}};
        when(config.getValue(new FieldName("TransformAndSetValue"))).thenReturn(rules);
        IObject env = mock(IObject.class);
        IObject response = mock(IObject.class);
        IObject message = mock(IObject.class);
        when(env.getValue(new FieldName("response"))).thenReturn(response);
        when(env.getValue(new FieldName("message"))).thenReturn(message);
        when(message.getValue(new FieldName("stringValue"))).thenReturn("1");
        when(rule11.getValue(new FieldName("name"))).thenReturn("wds_target_strategy");
        when(rule11.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("response/stringValue");}});
        when(rule21.getValue(new FieldName("name"))).thenReturn("TransformToInt");
        when(rule21.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("consts/2"); add("message/stringValue");}});
        when(rule22.getValue(new FieldName("name"))).thenReturn("wds_target_strategy");
        when(rule22.getValue(new FieldName("args"))).thenReturn(new ArrayList<String>(){{add("local/value"); add("response/intValue");}});
        doNothing().when(response).setValue(new FieldName("stringValue"), "1");
        doNothing().when(response).setValue(new FieldName("intValue"), 1);
        IObject wObj = new WDSObject(config);
        ((IObjectWrapper) wObj).init(env);
        wObj.setValue(new FieldName("TransformAndSetValue"), "1");
        verify(response, times(1)).setValue(new FieldName("stringValue"), "1");
        verify(response, times(1)).setValue(new FieldName("intValue"), 21);
    }
}
