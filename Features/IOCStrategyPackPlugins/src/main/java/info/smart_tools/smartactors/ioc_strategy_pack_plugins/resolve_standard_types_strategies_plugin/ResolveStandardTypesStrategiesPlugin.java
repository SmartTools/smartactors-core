package info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_standard_types_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunctionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.base.strategy.strategy_storage_with_cache_strategy.StrategyStorageWithCacheStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.DoubleToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.FloatToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.IntegerToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal.StringToBigDecimalStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_boolean_strategies.BooleanToPrimitiveStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_character_strategies.StringToCharacterStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_date_strategies.StringToDateStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies.DoubleToIntStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_integer_strategies.StringToIntStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.BooleanArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.ByteArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.CharArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.DoubleArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.FloatArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.IntArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.LongArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.ObjectArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_list_strategies.ShortArrayToListStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_long.IntegerToLongStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_long.StringToLongStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_self.ClassToClassStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.BooleanToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.ByteToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.CharToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.DoubleToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.FloatToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.IntToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.LongToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.ObjectToStringStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_string_strategies.ShortToStringStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Plugin.
 * Implements {@link IPlugin}
 * Load strategies for resolving standard types
 */
public class ResolveStandardTypesStrategiesPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public ResolveStandardTypesStrategiesPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            IBootstrapItem<String> item = new BootstrapItem("ResolveStandardTypesStrategiesPlugin");
            item
                    .before("read_initial_config")
                    .after("IOC")
                    .process(() -> {
                        try {
                            IFunction argToKey = arg -> arg.getClass();
                            IFunctionTwoArgs findValueByArgument = (map, arg) -> {
                                IStrategy strategy = null;
                                for (Map.Entry<Class, IStrategy> entry : ((Map<Class, IStrategy>) map).entrySet()) {
                                    if (entry.getKey().isInstance(arg)) {
                                        strategy = entry.getValue();

                                        break;
                                    }
                                }
                                return strategy;
                            };

                            // to String strategies
                            IKey stringKey = Keys.getKeyByName(String.class.getCanonicalName() + "convert");
                            IKey expandableStrategyStringKey = Keys.getKeyByName("expandable_strategy#" + String.class.getCanonicalName());
                            IStrategy stringStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration)stringStrategy).register(
                                    String.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    Object.class,
                                    new ObjectToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    int.class,
                                    new IntToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    long.class,
                                    new LongToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    float.class,
                                    new FloatToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    double.class,
                                    new DoubleToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    boolean.class,
                                    new BooleanToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    byte.class,
                                    new ByteToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    short.class,
                                    new ShortToStringStrategy()
                            );
                            ((IStrategyRegistration)stringStrategy).register(
                                    char.class,
                                    new CharToStringStrategy()
                            );
                            IOC.register(stringKey, stringStrategy);
                            IOC.register(expandableStrategyStringKey, new SingletonStrategy(stringStrategy));

                            // to Character strategies
                            IKey characterKey = Keys.getKeyByName(Character.class.getCanonicalName() + "convert");
                            IKey expandableStrategyCharacterKey = Keys.getKeyByName("expandable_strategy#" + Character.class.getCanonicalName());
                            IStrategy characterStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) characterStrategy).register(
                                    Character.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) characterStrategy).register(
                                    String.class,
                                    new StringToCharacterStrategy()
                            );
                            IOC.register(characterKey, characterStrategy);
                            IOC.register(expandableStrategyCharacterKey, new SingletonStrategy(characterStrategy));

                            // to boolean strategies
                            IKey booleanKey = Keys.getKeyByName(boolean.class.getCanonicalName() + "convert");
                            IKey expandableStrategyBooleanKey = Keys.getKeyByName("expandable_strategy#" + boolean.class.getCanonicalName());
                            IStrategy booleanStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) booleanStrategy).register(
                                    boolean.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) booleanStrategy).register(
                                    Boolean.class,
                                    new BooleanToPrimitiveStrategy()
                            );
                            IOC.register(booleanKey, booleanStrategy);
                            IOC.register(expandableStrategyBooleanKey, new SingletonStrategy(booleanStrategy));

                            // to Integer strategies
                            IKey integerKey = Keys.getKeyByName(Integer.class.getCanonicalName() + "convert");
                            IKey expandableStrategyIntegerKey = Keys.getKeyByName("expandable_strategy#" + Integer.class.getCanonicalName());
                            IStrategy integerStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) integerStrategy).register(
                                    Integer.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) integerStrategy).register(
                                    String.class,
                                    new StringToIntStrategy()
                            );
                            ((IStrategyRegistration) integerStrategy).register(
                                    Double.class,
                                    new DoubleToIntStrategy()
                            );
                            IOC.register(integerKey, integerStrategy);
                            IOC.register(expandableStrategyIntegerKey, new SingletonStrategy(integerStrategy));

                            //to long strategies
                                IKey longClassKey = Keys.getKeyByName(Long.class.getCanonicalName() + "convert");
                            IKey expandableStrategyLongClassKey = Keys.getKeyByName("expandable_strategy#" + Long.class.getCanonicalName());
                            IStrategy longClassStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) longClassStrategy).register(
                                Long.class,
                                new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) longClassStrategy).register(
                                String.class,
                                new StringToLongStrategy()
                            );
                            ((IStrategyRegistration) longClassStrategy).register(
                                Integer.class,
                                new IntegerToLongStrategy()
                            );
                            IOC.register(longClassKey, longClassStrategy);
                            IOC.register(expandableStrategyLongClassKey, new SingletonStrategy(longClassStrategy));

                            // to BigDecimal strategies
                            IKey bigDecimalKey = Keys.getKeyByName(BigDecimal.class.getCanonicalName() + "convert");
                            IKey expandableStrategyBigDecimalKey = Keys.getKeyByName("expandable_strategy#" + BigDecimal.class.getCanonicalName());
                            IStrategy bigDecimalStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) bigDecimalStrategy).register(
                                    BigDecimal.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) bigDecimalStrategy).register(
                                    String.class,
                                    new StringToBigDecimalStrategy()
                            );
                            ((IStrategyRegistration) bigDecimalStrategy).register(
                                    Double.class,
                                    new DoubleToBigDecimalStrategy()
                            );
                            ((IStrategyRegistration) bigDecimalStrategy).register(
                                    Float.class,
                                    new FloatToBigDecimalStrategy()
                            );
                            ((IStrategyRegistration) bigDecimalStrategy).register(
                                    Integer.class,
                                    new IntegerToBigDecimalStrategy()
                            );
                            IOC.register(bigDecimalKey, bigDecimalStrategy);
                            IOC.register(expandableStrategyBigDecimalKey, new SingletonStrategy(bigDecimalStrategy));

                            // to LocalDateTime strategies
                            IKey localDateTimeKey = Keys.getKeyByName(LocalDateTime.class.getCanonicalName() + "convert");
                            IKey expandableStrategyLocalDateTimeKey = Keys.getKeyByName("expandable_strategy#" + LocalDateTime.class.getCanonicalName());
                            IStrategy localDateTimeStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) localDateTimeStrategy).register(
                                    LocalDateTime.class,
                                    new ClassToClassStrategy()
                            );
                            ((IStrategyRegistration) localDateTimeStrategy).register(
                                    String.class,
                                    new StringToDateStrategy()
                            );
                            IOC.register(localDateTimeKey, localDateTimeStrategy);
                            IOC.register(expandableStrategyLocalDateTimeKey, new SingletonStrategy(localDateTimeStrategy));

                            // to list strategies
                            IKey listKey = Keys.getKeyByName(List.class.getCanonicalName() + "convert");
                            IKey expandableStrategyListKey = Keys.getKeyByName("expandable_strategy#" + List.class.getCanonicalName());
                            IStrategy listStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) listStrategy).register(boolean[].class, new BooleanArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(byte[].class, new ByteArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(char[].class, new CharArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(double[].class, new DoubleArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(float[].class, new FloatArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(int[].class, new IntArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(long[].class, new LongArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(Object[].class, new ObjectArrayToListStrategy());
                            ((IStrategyRegistration) listStrategy).register(short[].class, new ShortArrayToListStrategy());
                            IOC.register(listKey, listStrategy);
                            IOC.register(expandableStrategyListKey, new SingletonStrategy(listStrategy));

                            //to int strategies
                            IKey intKey = Keys.getKeyByName(int.class.getCanonicalName() + "convert");
                            IKey expandableStrategyIntKey = Keys.getKeyByName("expandable_strategy#" + int.class.getCanonicalName());
                            IStrategy intStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) intStrategy).register(
                                    String.class,
                                    new StringToIntStrategy()
                            );
                            ((IStrategyRegistration) intStrategy).register(
                                    Double.class,
                                    new DoubleToIntStrategy()
                            );
                            IOC.register(intKey, intStrategy);
                            IOC.register(expandableStrategyIntKey, new SingletonStrategy(intStrategy));

                            //to long strategies
                            IKey longKey = Keys.getKeyByName(long.class.getCanonicalName() + "convert");
                            IKey expandableStrategyLongKey = Keys.getKeyByName("expandable_strategy#" + long.class.getCanonicalName());
                            IStrategy longStrategy = new StrategyStorageWithCacheStrategy(argToKey, findValueByArgument);
                            ((IStrategyRegistration) longStrategy).register(
                                String.class,
                                new StringToLongStrategy()
                            );
                            ((IStrategyRegistration) longStrategy).register(
                                Integer.class,
                                new IntegerToLongStrategy()
                            );
                            IOC.register(longKey, longStrategy);
                            IOC.register(expandableStrategyLongKey, new SingletonStrategy(longStrategy));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("ResolveStandardTypesStrategies plugin can't load: can't get ResolveStandardTypesStrategies key", e);
                        } catch (RegistrationException | StrategyRegistrationException | InvalidArgumentException e) {
                            throw new ActionExecutionException("ResolveStandardTypesStrategies plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = {
                                "expandable_strategy#" + int.class.getCanonicalName(),
                                int.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + List.class.getCanonicalName(),
                                List.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + LocalDateTime.class.getCanonicalName(),
                                LocalDateTime.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + BigDecimal.class.getCanonicalName(),
                                BigDecimal.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + Integer.class.getCanonicalName(),
                                Integer.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + long.class.getCanonicalName(),
                                String.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + Long.class.getCanonicalName(),
                                String.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + boolean.class.getCanonicalName(),
                                boolean.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + Character.class.getCanonicalName(),
                                Character.class.getCanonicalName() + "convert",
                                "expandable_strategy#" + String.class.getCanonicalName(),
                                String.class.getCanonicalName() + "convert"
                        };
                        Keys.unregisterByNames(keyNames);
                    });

            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load ResolveStandardTypesStrategies plugin", e);
        }
    }
}
