package info.smart_tools.smartactors.ioc.recursive_strategy_container;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 *  Test how IoC with this strategy container works in complex, with nested scope.
 */
public class StrategyContainerScopeTest {

    private IScope parentScope;
    private IScope childScope;

    @Before
    public void setup() throws ScopeProviderException, InvalidArgumentException, RegistrationException {
        ScopeProvider.clearListOfSubscribers();
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
    public void testSystemIOC()
            throws Exception {
        IKey key = Keys.getKeyByName("test");

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
        IOC.unregister(key);

        ScopeProvider.setCurrentScope(parentScope);
        assertSame(parentObject, IOC.resolve(key));
        ScopeProvider.setCurrentScope(childScope);
        assertSame(parentObject, IOC.resolve(key));

        ScopeProvider.setCurrentScope(parentScope);
        IOC.unregister(key);

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
