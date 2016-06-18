package info.smart_tools.smartactors.core.examples;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.exception.ScopeException;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
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
        IOC.register(IOC.getKeyForKeyStorage(), new ResolveByNameIocStrategy(
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
        IKey<MyClass> myResolveKey = IOC.resolve(IOC.getKeyForKeyStorage(), "myKey");
        IKey<MyClass> myNewKey = new Key<>("myKey");
        IKey<MyClass> myTypedKey = new Key<>(MyClass.class, "myKey");
        assertEquals("new differs from resolve", myNewKey, myResolveKey);
    }

    @Test
    public void singletonStrategyExample() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IKey<MyClass> key = IOC.resolve(IOC.getKeyForKeyStorage(), "singleton");
        MyClass myObject = new MyClass("singleton");
        IOC.register(key, new SingletonStrategy(myObject));
        MyClass resolveObject1 = IOC.resolve(key);
        MyClass resolveObject2 = IOC.resolve(key);
        assertEquals("first resolve not equals", myObject, resolveObject1);
        assertEquals("second resolve not equals", myObject, resolveObject2);
        assertSame("first resolve not same", myObject, resolveObject1);
        assertSame("second resolve not same", myObject, resolveObject2);
    }

    @Test
    public void createNewInstanceStrategyExample() throws ResolutionException, RegistrationException, InvalidArgumentException {
        IKey<MyClass> key = IOC.resolve(IOC.getKeyForKeyStorage(), "new");
        IOC.register(key, new CreateNewInstanceStrategy(
                (args) -> new MyClass((String) args[0])));
        MyClass resolveObject1 = IOC.resolve(key, "id1");
        MyClass resolveObject2 = IOC.resolve(key, "id1");
        MyClass resolveObject3 = IOC.resolve(key, "id3");
        assertEquals("objects with the same ids are not equals", resolveObject1, resolveObject2);
        assertNotSame("objects with the same ids are same", resolveObject1, resolveObject2);
        assertTrue("different objects are equals", !resolveObject1.equals(resolveObject3));
        assertNotSame("objects with different ids are same", resolveObject1, resolveObject3);
    }
    
}
