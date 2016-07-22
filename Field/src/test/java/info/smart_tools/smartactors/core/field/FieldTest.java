
package info.smart_tools.smartactors.core.field;


import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        when(strategy.resolve(1)).thenReturn("1");
        IOC.register(Keys.getOrAdd(String.class.getCanonicalName()+Integer.class.getCanonicalName()), strategy);
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

