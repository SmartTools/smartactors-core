
package info.smart_tools.smartactors.field.field;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
    }

    @Test
    public void checkFieldCreation()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        assertNotNull(field);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnFieldCreation()
            throws Exception {
        IField field = new Field(null);
        fail();
    }

    @Test
    public void checkInMethod()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        IObject env = mock(IObject.class);
        when(env.getValue(new FieldName("a"))).thenReturn(true);
        assertTrue(field.in(env));
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodExceptionOnNullArgument()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        field.in(null);
        fail();
    }

    @Test
    public void checkOutMethod()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        IObject env = mock(IObject.class);
        doNothing().when(env).setValue(new FieldName("a"), 1);
        field.out(env, 1);
        verify(env, times(1)).setValue(new FieldName("a"), 1);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutMethodExceptionOnNullArgument()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        field.out(null, 1);
        fail();
    }

    @Test
    public void checkInMethodWithTypeCast()
            throws Exception {
        IStrategy strategy = mock(IStrategy.class);
        when(strategy.resolve(1)).thenReturn("1");
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), String.class.getCanonicalName() + "convert"),
                strategy
        );
        IField field = new Field(new FieldName("a"));
        IObject env = mock(IObject.class);
        when(env.getValue(new FieldName("a"))).thenReturn(1);

        String result = field.in(env, String.class);
        assertEquals(result, "1");
    }

    @Test
    public void checkInMethodWithTypeCastOnEqualRequiredTypeWithExisting()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        IObject env = mock(IObject.class);
        when(env.getValue(new FieldName("a"))).thenReturn("1");

        String result = field.in(env, String.class);
        assertEquals(result, "1");
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodWithTypeCastExceptionOnAbsentStrategy()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        IObject env = mock(IObject.class);
        when(env.getValue(new FieldName("a"))).thenReturn(1);
        field.in(env, String.class);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodWithTypeCastExceptionOnNullFirstArgument()
            throws Exception {
        IField field = new Field(new FieldName("a"));
        field.in(null, String.class);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInMethodWithTypeCastExceptionOnNullSecondArgument()
            throws Exception {
        IObject env = mock(IObject.class);
        IField field = new Field(new FieldName("a"));
        field.in(env, null);
        fail();
    }

}

