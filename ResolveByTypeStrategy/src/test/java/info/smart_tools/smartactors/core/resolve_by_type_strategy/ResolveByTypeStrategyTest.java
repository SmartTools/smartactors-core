package info.smart_tools.smartactors.core.resolve_by_type_strategy;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ResolveByTypeStrategyTest {

    @Test
    public void checkStrategyCreation() {
        IResolveDependencyStrategy strategy = new ResolveByTypeStrategy();
        assertNotNull(strategy);
    }

    @Test (expected = ResolveDependencyStrategyException.class)
    public void checkStrategyResolutionException()
            throws Exception {
        IResolveDependencyStrategy strategy = new ResolveByTypeStrategy();
        strategy.resolve(null);
    }
}
