package info.smart_tools.smartactors.core.recursive_strategy_container;

import info.smart_tools.smartactors.core.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.exception.ScopeException;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 *  Test how IoC with this strategy container works in complex, with nested scope.
 */
public class StrategyContainerScopeTest {

    private IScope parentScope;
    private IScope childScope;

    @Before
    public void setUp() throws ScopeProviderException, InvalidArgumentException, RegistrationException {
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        IStrategyContainer parentContainer = null;
                        try {
                            parentContainer = (IStrategyContainer) scope.getValue(IOC.getIocKey());
                        } catch (ScopeException e) {
                            // parent container does not exists, create a new with null parent
                        }
                        scope.setValue(IOC.getIocKey(), new StrategyContainer(parentContainer));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        Object parentScopeKey = ScopeProvider.createScope(null);
        parentScope = ScopeProvider.getScope(parentScopeKey);
        Object childScopeKey = ScopeProvider.createScope(parentScope);
        childScope = ScopeProvider.getScope(childScopeKey);

        ScopeProvider.setCurrentScope(parentScope);
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
    public void testSystemIOC() throws ScopeProviderException, ResolutionException, InvalidArgumentException, RegistrationException, DeletionException {
        IKey key = Keys.getOrAdd("test");

        ScopeProvider.setCurrentScope(parentScope);
        try {
            IOC.resolve(key);
            fail();
        } catch (ResolutionException e) {
            // no strategy
        }
        ScopeProvider.setCurrentScope(childScope);
        try {
            IOC.resolve(key);
            fail();
        } catch (ResolutionException e) {
            // no strategy
        }

        ScopeProvider.setCurrentScope(parentScope);
        Object parentObject = new Object();
        IOC.register(key, new SingletonStrategy(parentObject));

        ScopeProvider.setCurrentScope(parentScope);
        assertSame(parentObject, IOC.resolve(key));
        ScopeProvider.setCurrentScope(childScope);
        assertSame(parentObject, IOC.resolve(key));

        ScopeProvider.setCurrentScope(childScope);
        Object childObject = new Object();
        IOC.register(key, new SingletonStrategy(childObject));

        ScopeProvider.setCurrentScope(parentScope);
        assertSame(parentObject, IOC.resolve(key));
        ScopeProvider.setCurrentScope(childScope);
        assertSame(childObject, IOC.resolve(key));

        ScopeProvider.setCurrentScope(childScope);
        IOC.remove(key);

        ScopeProvider.setCurrentScope(parentScope);
        assertSame(parentObject, IOC.resolve(key));
        ScopeProvider.setCurrentScope(childScope);
        assertSame(parentObject, IOC.resolve(key));

        ScopeProvider.setCurrentScope(parentScope);
        IOC.remove(key);

        ScopeProvider.setCurrentScope(parentScope);
        try {
            IOC.resolve(key);
            fail();
        } catch (ResolutionException e) {
            // no strategy
        }
        ScopeProvider.setCurrentScope(childScope);
        try {
            IOC.resolve(key);
            fail();
        } catch (ResolutionException e) {
            // no strategy
        }
    }

}
