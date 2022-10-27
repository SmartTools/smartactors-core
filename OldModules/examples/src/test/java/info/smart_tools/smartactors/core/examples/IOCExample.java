package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is example of usage of IoC.
 */
public class IOCExample {

    @Before
    public void IOCInitialization() throws RegistrationException, ScopeProviderException, ScopeException, InvalidArgumentException {
        Object scopeKey = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(scopeKey);
        ScopeProvider.setCurrentScope(scope);
        scope.setValue(IOC.getIocKey(), new StrategyContainer());
        IOC.register(IOC.getKeyForKeyByNameStrategy(), new ResolveByNameIocStrategy(
                (a) -> {
                    try {
                        return new Key((String) a[0]);
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }
    
    @Test
    public void keyExample() throws ResolutionException, InvalidArgumentException {
        IKey resolveKey = IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "sample");
        IKey key = Keys.getKeyByName("sample");
        IKey newKey = new Key("sample");
        assertEquals("resolve differs from got from Keys", resolveKey, key);
        assertEquals("new differs from resolve", newKey, resolveKey);
    }

    @Test
    public void singletonStrategyExample() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IKey key = Keys.getKeyByName("singleton");
        SampleClass sampleObject = new SampleClass("singleton");
        IOC.register(key, new SingletonStrategy(sampleObject));
        SampleClass resolveObject1 = IOC.resolve(key);
        SampleClass resolveObject2 = IOC.resolve(key);
        assertEquals("first resolve not equals", sampleObject, resolveObject1);
        assertEquals("second resolve not equals", sampleObject, resolveObject2);
        assertSame("first resolve not same", sampleObject, resolveObject1);
        assertSame("second resolve not same", sampleObject, resolveObject2);
    }

    @Test
    public void createNewInstanceStrategyExample() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IKey key = Keys.getKeyByName("new");
        IOC.register(key, new CreateNewInstanceStrategy(
                (args) -> new SampleClass((String) args[0])));
        SampleClass resolveObject1 = IOC.resolve(key, "id1");
        SampleClass resolveObject2 = IOC.resolve(key, "id1");
        SampleClass resolveObject3 = IOC.resolve(key, "id3");
        assertEquals("objects with the same ids are not equals", resolveObject1, resolveObject2);
        assertNotSame("objects with the same ids are same", resolveObject1, resolveObject2);
        assertTrue("different objects are equals", !resolveObject1.equals(resolveObject3));
        assertNotSame("objects with different ids are same", resolveObject1, resolveObject3);
    }
    
}
