package info.smart_tools.smartactors.helpers.IOCInitializer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class IOCInitializer {

    static {
        try {
            ScopeProvider.subscribeOnCreationNewScope(scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, IActionNoArgs> strategies = new HashMap<String, IActionNoArgs>() {{
        put(
                "new scope",
                () -> {
                    try {
                        Object systemScopeKey = ScopeProvider.createScope(null);
                        IScope scope = ScopeProvider.getScope(systemScopeKey);
                        ScopeProvider.setCurrentScope(scope);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        put(
                "key strategy",
                () -> {
                    try {
                        IOC.register(
                                IOC.getKeyForKeyByNameStrategy(),
                                new ResolveByNameIocStrategy(
                                        (a) -> {
                                            try {
                                                return new Key((String) a[0]);
                                            } catch (InvalidArgumentException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        put(
                "ifieldname strategy",
                () -> {
                    try {
                        IOC.register(
                                Keys.getKeyByName(IFieldName.class.getCanonicalName()),
                                new ResolveByNameIocStrategy(
                                        (args) -> {
                                            try {
                                                String nameOfFieldName = (String) args[0];
                                                return new FieldName(nameOfFieldName);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                        );
                        IOC.register(
                                Keys.getKeyByName(FieldName.class.getCanonicalName()),
                                new ResolveByNameIocStrategy(
                                        (args) -> {
                                            try {
                                                String nameOfFieldName = (String) args[0];
                                                return new FieldName(nameOfFieldName);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        put(
                "iobject strategy",
                () -> {
                    try {
                        IOC.register(
                                Keys.getKeyByName(IObject.class.getCanonicalName()),
                                new ApplyFunctionToArgumentsStrategy(
                                        args -> {
                                            if (args.length == 0) {
                                                return new DSObject();
                                            } else if (args.length == 1 && args[0] instanceof String) {
                                                try {
                                                    return new DSObject((String) args[0]);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            } else if (args.length == 1 && args[0] instanceof Map) {
                                                try {
                                                    return new DSObject((Map) args[0]);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            } else {
                                                throw new RuntimeException("Invalid arguments for IObject creation.");
                                            }
                                        }
                                )
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }};

    @Before
    public synchronized void before()
            throws Exception {
        registryStrategies("new scope", "key strategy");
        registry();
        registerMocks();
    }

    protected abstract void registry(final String ... strategyNames)
            throws Exception;

    protected void registryStrategies(final String ... strategyNames)
            throws Exception {

        Arrays.stream(strategyNames).forEach(
                (strategyName) -> {
                    try {
                        this.strategies.get(strategyName).execute();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    protected void registerMocks() throws Exception {}
}
