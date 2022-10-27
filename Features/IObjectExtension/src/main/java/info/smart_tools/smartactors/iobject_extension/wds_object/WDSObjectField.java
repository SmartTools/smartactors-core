package info.smart_tools.smartactors.iobject_extension.wds_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class WDSObjectField implements IField {

    private static final String FIELD_ARGS = "args";
    private static final String FIELD_NAME = "name";
    private static final String LOCAL_KEYWORD = "local";
    private static final String CONST_KEYWORD = "const";
    private static final String DEFAULT_KEYWORD = "default";
    private static final String SPLITTER = "\\/";
    private static final String INNER_SETTER_STRATEGY_NAME = "wds_target_strategy";
    private static final String INNER_GETTER_STRATEGY_NAME = "wds_getter_strategy";
    private static final String OUTER_STRATEGY_NAME = "wds_outer_strategy";
    private static final String SLASH = "/";

    private IFieldName args;
    private IFieldName strategyName;
    private List<IObject> rules;
    private HashMap<String, IStrategy> strategies = new HashMap<>();
    private HashMap<String, IFunction<StrategyAndArgs, Object>> strategyExecutors = new HashMap<>();
    private HashMap<String, IFunction<Arg, Object>> argumentsResolvers = new HashMap<>();

    /**
     * Constructor.
     * Create instance of {@link WDSObjectField}
     * @param methodRules the configuration of current method
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    public WDSObjectField(final List<IObject> methodRules)
            throws InvalidArgumentException {
        if (null == methodRules) {
            throw new InvalidArgumentException("Method configuration should not be null.");
        }
        try {
            initializeStrategyExecutors();
            initializeArgumentsResolvers();
            this.strategyName = IOC.resolve(
                    Keys.getKeyByName(IFieldName.class.getCanonicalName()), FIELD_NAME
            );
            this.args = IOC.resolve(
                    Keys.getKeyByName(IFieldName.class.getCanonicalName()), FIELD_ARGS
            );
            this.rules = methodRules;
            for (IObject rule : rules) {
                String name = (String) rule.getValue(this.strategyName);
                strategies.put(
                        name,
                        null == this.strategyExecutors.get(name) ?
                                IOC.resolve(Keys.getKeyByName(IStrategy.class.getCanonicalName()), name) :
                                null
                );
            }
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not create instance of WDSObjectField." , e);
        }
    }

    @Override
    public <T> T in(final IObject env)
            throws ReadValueException, InvalidArgumentException {
        if (null == env) {
            throw new InvalidArgumentException("Environment should not be null.");
        }
        Object value = null;
        try {
            for (IObject rule : this.rules) {
                List<String> args = (ArrayList<String>) rule.getValue(this.args);
                String name = (String) rule.getValue(this.strategyName);
                IFunction<StrategyAndArgs, Object> func = this.strategyExecutors.get(name);
                if (null == func) {
                    func = this.strategyExecutors.get(OUTER_STRATEGY_NAME);
                }
                value = func.execute(
                        new StrategyAndArgs(this.strategies.get(name), env, args, value)
                );
            }
        } catch (Exception e) {
            throw new ReadValueException("Could not apply rules.", e);
        }
        if (null == value) {
            return null;
        }

        return (T) value;
    }

    @Override
    public <T> T in(final IObject env, final Class type)
            throws ReadValueException, InvalidArgumentException {
        throw new ReadValueException("Method not implemented.");
    }

    @Override
    public <T> void out(final IObject env, final T in)
            throws ChangeValueException, InvalidArgumentException {
        if (null == env) {
            throw new InvalidArgumentException("Environment should not be null.");
        }
        Object value = in;
        try {
            for (IObject rule : rules) {
                List<String> args = (ArrayList<String>) rule.getValue(this.args);
                String name = (String) rule.getValue(this.strategyName);
                IFunction<StrategyAndArgs, Object> func = this.strategyExecutors.get(name);
                if (null == func) {
                    func = this.strategyExecutors.get(OUTER_STRATEGY_NAME);
                }
                value = func.execute(
                        new StrategyAndArgs(this.strategies.get(name), env, args, value)
                );
            }
        } catch (Exception e) {
            throw new ChangeValueException("Could not apply rules.", e);
        }
    }

    private Object getValueFromNestedIObject(final IObject source, final String location)
            throws ReadValueException {
        try {
            String[] separated = location.split(SPLITTER);
            if (separated.length == 1) {
                return source.getValue(IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), separated[0]));
            }
            return getNestedIObject(source, separated).getValue(
                    IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), separated[separated.length - 1])
            );
        } catch (Throwable e) {
            throw new ReadValueException("Could not read " + location + " from IObject", e);
        }
    }

    private IObject getNestedIObject(final IObject source, final String[] separatedPath)
            throws Exception {
        try {
            IObject nestedObject = (IObject) source.getValue(
                    IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), separatedPath[0])
            );
            for (int i = 1; i < separatedPath.length - 1; ++i) {
                nestedObject = (IObject) nestedObject.getValue(
                        IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), separatedPath[i])
                );
            }

            return nestedObject;
        } catch (Throwable e) {
            throw new ReadValueException("Could not read nested IObject.", e);
        }
    }

    private void initializeStrategyExecutors() {
        this.strategyExecutors.put(
                INNER_SETTER_STRATEGY_NAME,
                new IFunction<StrategyAndArgs, Object>() {
                    @Override
                    public Object execute(final StrategyAndArgs o)
                            throws FunctionExecutionException, InvalidArgumentException {
                        try {
                            Object value = resolveSingleArgument(o.getArgs().get(0), o.getEnv(), o.getLocalValue());
                            String source = o.getArgs().get(1);
                            String[] split = source.split(SPLITTER);
                            String fieldName = split[split.length - 1];
                            String path = source.substring(0, source.lastIndexOf(SLASH));
                            ((IObject) getValueFromNestedIObject(o.getEnv(), path)).setValue(
                                    IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), fieldName), value
                            );

                            return null;
                        } catch (Throwable e) {
                            throw new FunctionExecutionException("Could not execute function.", e);
                        }
                    }
                }
        );
        this.strategyExecutors.put(
                INNER_GETTER_STRATEGY_NAME,
                new IFunction<StrategyAndArgs, Object>() {
                    @Override
                    public Object execute(final StrategyAndArgs o)
                            throws FunctionExecutionException, InvalidArgumentException {
                        try {
                            return resolveSingleArgument(o.getArgs().get(0), o.getEnv(), o.getLocalValue());
                        } catch (Throwable e) {
                            throw new FunctionExecutionException("Could not execute function.", e);
                        }
                    }
                }
        );
        this.strategyExecutors.put(
                OUTER_STRATEGY_NAME,
                new IFunction<StrategyAndArgs, Object>() {
                    @Override
                    public Object execute(final StrategyAndArgs o)
                            throws FunctionExecutionException, InvalidArgumentException {
                        try {
                            return o.getStrategy().resolve(resolveArguments(
                                    o.getArgs(), o.getEnv(), o.getLocalValue())
                            );
                        } catch (Throwable e) {
                            throw new FunctionExecutionException("Could not execute function.", e);
                        }
                    }
                }
        );
    }

    private void initializeArgumentsResolvers() {
        this.argumentsResolvers.put(
                LOCAL_KEYWORD,
                new IFunction<Arg, Object>() {
                    @Override
                    public Object execute(final Arg o) throws FunctionExecutionException, InvalidArgumentException {
                        return o.getLocalValue();
                    }
                }
        );
        this.argumentsResolvers.put(
                CONST_KEYWORD,
                new IFunction<Arg, Object>() {
                    @Override
                    public Object execute(final Arg o) throws FunctionExecutionException, InvalidArgumentException {
                        return o.getArg().split(SPLITTER, 2)[1];
                    }
                }
        );
        this.argumentsResolvers.put(
                DEFAULT_KEYWORD,
                new IFunction<Arg, Object>() {
                    @Override
                    public Object execute(final Arg o) throws FunctionExecutionException, InvalidArgumentException {
                        try {
                            return getValueFromNestedIObject(o.getEnv(), o.getArg());
                        } catch (Throwable e) {
                            throw new InvalidArgumentException("Could not get value from IObject", e);
                        }
                    }
                }
        );
    }

    private Object[] resolveArguments(
            final List<String> arguments,
            final IObject env,
            final Object localValue
    ) throws InvalidArgumentException {
        Object[] resolvedArgs = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); ++i) {
            resolvedArgs[i] = resolveSingleArgument(arguments.get(i), env, localValue);
        }
        return resolvedArgs;
    }

    private Object resolveSingleArgument(final String arg, final IObject env, final Object localValue)
            throws InvalidArgumentException {
        String key = arg.split(SPLITTER)[0];
        IFunction<Arg, Object> func = this.argumentsResolvers.get(key);
        if (null == func) {
            func = this.argumentsResolvers.get(DEFAULT_KEYWORD);
        }
        try {
            return func.execute(new Arg(arg, env, localValue));
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not parse strategy argument.", e);
        }
    }
}

/**
 * Support class for pack some arguments to single
 */
class StrategyAndArgs {
    private IStrategy strategy;
    private List<String> args;
    private IObject env;
    private Object localValue;

    /**
     * Constructor.
     * Creates new instance of {@link StrategyAndArgs} by follow params
     * @param strategy instance of {@link IStrategy}
     * @param env the environment object
     * @param args unresolved strategy arguments
     * @param localValue the result of last transformation rule execution or argument of {@code out} method
     */
    StrategyAndArgs(
            final IStrategy strategy,
            final IObject env,
            final List<String> args, final Object localValue
    ) {
        this.strategy = strategy;
        this.args = args;
        this.env = env;
        this.localValue = localValue;
    }

    IStrategy getStrategy() {
        return this.strategy;
    }

    List<String> getArgs() {
        return this.args;
    }

    IObject getEnv() {
        return env;
    }

    Object getLocalValue() {
        return localValue;
    }
}

/**
 * Support class for pack some arguments to single
 */
class Arg {
    private String arg;
    private Object localValue;
    private IObject env;

    /**
     * Constructor.
     * Creates new instance of {@link Arg} by follow params
     * @param arg unresolved strategy argument
     * @param env the environment object
     * @param localValue the result of last transformation rule execution or argument of {@code out} method
     */
    Arg(
            final String arg,
            final IObject env,
            final Object localValue
    ) {
        this.arg = arg;
        this.localValue = localValue;
        this.env = env;
    }

    String getArg() {
        return this.arg;
    }

    Object getLocalValue() {
        return this.localValue;
    }

    IObject getEnv() {
        return this.env;
    }
}


