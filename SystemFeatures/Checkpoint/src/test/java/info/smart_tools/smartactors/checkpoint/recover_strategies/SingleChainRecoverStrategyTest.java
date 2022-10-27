package info.smart_tools.smartactors.checkpoint.recover_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice.IRecoveryChainChoiceStrategy;
import info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice.SingleChainRecoverStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link SingleChainRecoverStrategy}.
 */
public class SingleChainRecoverStrategyTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }
    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) args[0].toString().concat("__0");
            }
        });
    }

    @Test
    public void Should_alwaysReturnTheSameChainForAStateObject()
            throws Exception {
        IRecoveryChainChoiceStrategy strategy = new SingleChainRecoverStrategy();

        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'chain':'theChain'}".replace('\'','"'));

        strategy.init(state, args);

        assertEquals("theChain", strategy.chooseRecoveryChain(state));
        assertEquals("theChain", strategy.chooseRecoveryChain(state));
        assertEquals("theChain", strategy.chooseRecoveryChain(state));
    }

    @Test(expected = RecoverStrategyInitializationException.class)
    public void Should_throwWhenArgumentsContainNoChainName()
            throws Exception {
        IRecoveryChainChoiceStrategy strategy = new SingleChainRecoverStrategy();

        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        strategy.init(state, args);
    }
}
