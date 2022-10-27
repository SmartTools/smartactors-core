package info.smart_tools.smartactors.checkpoint.recover_strategies.chain_choice;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyExecutionException;
import info.smart_tools.smartactors.checkpoint.interfaces.exceptions.RecoverStrategyInitializationException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Strategy that sends message to the same chain every time.
 *
 * <pre>
 *     {
 *       "strategy": "single chain recover strategy",
 *       "chain": "recoveryChain" // Name of the chain where the message will be sent
 *     }
 * </pre>
 */
public class SingleChainRecoverStrategy implements IRecoveryChainChoiceStrategy {
    private final IFieldName chainNameFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public SingleChainRecoverStrategy()
            throws ResolutionException {
        chainNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
    }

    @Override
    public void init(final IObject state, final IObject args) throws RecoverStrategyInitializationException {
        try {
            String chainName = (String) args.getValue(chainNameFieldName);

            if (null == chainName) {
                throw new RecoverStrategyInitializationException("Chain name should be defined.", null);
            }

            state.setValue(chainNameFieldName, chainName);
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException e) {
            throw new RecoverStrategyInitializationException("Error occurred initializing strategy.", e);
        }
    }

    @Override
    public Object chooseRecoveryChain(final IObject state) throws RecoverStrategyExecutionException {
        try {
            // return IOC.resolve(Keys.getKeyByName("chain_id_from_map_name_and_message"), state.getValue(chainFieldName));
            return state.getValue(chainNameFieldName);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new RecoverStrategyExecutionException("Error occurred resolving chain identifier.", e);
        }
    }
}
