/*
package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
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
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

*
 * Tests for Field


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
                Keys.getOrAdd(IKey.class.getCanonicalName()),
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
        IOC.register(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> a[1]
                )
        );

        // Mocks for IObjects
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "environments");
        IObject testBinding = new DSObject("{\n" +
                "    \"in_getIntValue\": \"message/IntValue\",\n" +
                "    \"out_setIntValue\": \"response/IntValue\",\n" +
                "    \"in_getTestClassValue\": \"message/TestClassValue\",\n" +
                "    \"out_setTestClassValue\": \"response/TestClassValue\",\n" +
                "    \"out_setListOfTestClasses\": \"response/ListOfTestClasses\",\n" +
                "    \"out_wrappedIObject\": \"response/WrappedIObject\",\n" +
                "    \"in_wrappedIObject\": [{\n" +
                "      \"name\": \"ConvertToWrapper\",\n" +
                "      \"args\": [\"environment\", \"const/info.smart_tools.smartactors.core.wrapper_generator.IInnerWrapper\"]\n" +
                "    }],\n" +
                "    \"in_getListOfTestClasses\": [{\n" +
                "      \"args\": [\"message/TestClassValue\"]\n" +
                "    }, {\n" +
                "      \"name\": \"AddToList\",\n" +
                "      \"args\": [\"context/ListOfTestClasses\", \"local/value\"]\n" +
                "    }, {\n" +
                "      \"args\": [\"message/OtherTestClassValue\"]\n" +
                "    }, {\n" +
                "      \"name\": \"AddToList\",\n" +
                "      \"args\": [\"context/ListOfTestClasses\", \"local/value\"]\n" +
                "    }, {\n" +
                "      \"args\": [\"context/ListOfTestClasses\"]\n" +
                "    }],\n" +
                "    \"out_transform\": [\n" +
                "      [{\n" +
                "        \"name\": \"ConvertToString\",\n" +
                "        \"args\": [\"local/value\"]\n" +
                "      }, {\n" +
                "        \"name\": \"JoinStrings\",\n" +
                "        \"args\": [\"local/value\", \"const/abc\"]\n" +
                "      }, {\n" +
                "        \"name\": \"target\",\n" +
                "        \"args\": [\"response/ModifiedString1\", \"local/value\"]\n" +
                "      }],\n" +
                "      [{\n" +
                "        \"name\": \"ConvertToString\",\n" +
                "        \"args\": [\"local/value\"]\n" +
                "      }, {\n" +
                "        \"name\": \"target\",\n" +
                "        \"args\": [\"response/ModifiedString2\", \"local/value\"]\n" +
                "      }]\n" +
                "    ]\n" +
                "  }");
        IObject maps = new DSObject();
        maps.setValue(
                new FieldName("someInterface"),
                testBinding
        );
        env.setValue(new FieldName("wrappers"), maps);
        IObject message = mock(IObject.class);
        when(message.getValue(new FieldName("Value"))).thenReturn(1);

        // Register strategies
        IResolveDependencyStrategy toWrapperConverter = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ConvertToWrapper"), toWrapperConverter);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()), "ConvertToWrapper", toWrapperConverter
        );

        IResolveDependencyStrategy addToList = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("AddToList"), addToList);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()), "AddToList", addToList
        );

        IResolveDependencyStrategy convertToString = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("ConvertToString"), convertToString);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()), "ConvertToString", convertToString
        );

        IResolveDependencyStrategy joinStrings = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("JoinStrings"), joinStrings);
        IOC.resolve(
                Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()), "JoinStrings", joinStrings
        );

        IResolveDependencyStrategy targetStrategy =  new ApplyFunctionToArgumentsStrategy(
                (a) -> {
                    try {
                        ((IObject) a[0]).setValue(
                                IOC.resolve(
                                        Keys.getOrAdd(IKey.class.getCanonicalName()), (String) a[1]
                                ),
                                a[2]
                        );
                        return a[0];
                    } catch (Throwable e) {
                        throw new RuntimeException("Resolution error in 'target' strategy.");
                    }
                }
        );
        IOC.resolve(Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()), "target", targetStrategy);
    }

    @Test
    public void checkFieldCreation()
            throws Exception {
//        IField field1 = new Field("someInterface/in_getIntValue");
//        IField field2 = new Field("someInterface/in_getListOfTestClasses");
//        IField field3 = new Field("someInterface/out_setIntValue");
//        IField field4 = new Field("someInterface/out_transform");
//        assertNotNull(field1);
//        assertNotNull(field2);
//        assertNotNull(field3);
//        assertNotNull(field4);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInFieldOnWrongArgumentForInMethod()
            throws Exception {
//        IField field = new InField("someInterface/in_getIntValue");
//        field.in(null);
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutFieldOnWrongArgumentForOutMethod()
            throws Exception {
//        IField field = new OutField("someInterface/out_setIntValue");
//        field.out(null, null);
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInFieldOnNullBinding()
            throws Exception {
//        IField field = new InField(null);
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutFieldOnNullBinding()
            throws Exception {
//        IField field = new OutField(null);
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInFieldOnWrongBinding()
            throws Exception {
//        IField field = new InField("a/a");
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutFieldOnWrongBinding()
            throws Exception {
//        IField field = new OutField("a/a");
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInFieldOnUseOutMethod()
            throws Exception {
//        IField field = new InField("someInterface/in_getIntValue");
//        field.out(mock(IObject.class), null);
//        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkOutFieldOnUseInMethod()
            throws Exception {
//        IField field = new OutField("someInterface/out_setIntValue");
//        field.in(mock(IObject.class));
//        fail();
    }

}
*/
