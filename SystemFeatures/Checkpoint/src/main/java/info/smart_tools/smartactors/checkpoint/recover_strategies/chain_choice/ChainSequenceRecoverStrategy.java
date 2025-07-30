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

import java.util.List;

/**
 * Strategy that sends message to different chains.
 *
 * <p> Configuration example: </p>
 * <pre>
 *     {
 *       "strategy": "chain sequence recover strategy",
 *       "trials": [1, 2, 1, 3],                       // Number of re-send trials to switch to next chain after
 *       "chains": ["A", "B", "C", "D", "E"]           // Names of chains to use
 *
 *       // In this example the message will be re-sent to chain "A" once then two times to chain "B" then once to "C" then 3 times to "D"
 *       // and then to "E" all remaining times (of course the message will not be re-sent if the next checkpoint notifies this one)
 *     }
 * </pre>
 */
public class ChainSequenceRecoverStrategy implements IRecoveryChainChoiceStrategy {
    private final IFieldName trialsFieldName;
    private final IFieldName chainsFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public ChainSequenceRecoverStrategy()
            throws ResolutionException {
        trialsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "trials");
        chainsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chains");
    }

    @Override
    public void init(final IObject state, final IObject args) throws RecoverStrategyInitializationException {
        try {
            List trials = (List) args.getValue(trialsFieldName);
            List chains = (List) args.getValue(chainsFieldName);

            if (null == chains || null == trials) {
                throw new RecoverStrategyInitializationException(
                        "Strategy configuration should contain 'trials' and 'chains' fields.", null);
            }

            if (trials.size() != chains.size() - 1) {
                throw new RecoverStrategyInitializationException("Invalid 'trials' and 'chains' list sizes.", null);
            }

            state.setValue(trialsFieldName, trials);
            state.setValue(chainsFieldName, chains);
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
            throw new RecoverStrategyInitializationException("Error initializing recover strategy.", e);
        }
    }

    @Override
    public Object chooseRecoveryChain(final IObject state) throws RecoverStrategyExecutionException {
        try {
            List trials = (List) state.getValue(trialsFieldName);
            List chains = (List) state.getValue(chainsFieldName);
            Object chainName = chains.get(0);

            if (trials.size() != 0) {
                int t = ((Number) trials.get(0)).intValue() - 1;

                if (t == 0) {
                    trials.remove(0);
                    chains.remove(0);
                } else {
                    trials.set(0, t);
                }
            }
            // return IOC.resolve(Keys.getKeyByName("chain_id_from_map_name_and_message"), chainName);
            return chainName;
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new RecoverStrategyExecutionException("Error initializing recover strategy.", e);
        }
    }
}
