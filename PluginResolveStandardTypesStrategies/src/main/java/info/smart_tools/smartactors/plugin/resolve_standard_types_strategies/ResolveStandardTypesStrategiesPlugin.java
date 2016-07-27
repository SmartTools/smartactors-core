package info.smart_tools.smartactors.plugin.resolve_standard_types_strategies;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_type_strategy.ResolveByTypeStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal.DoubleToBigDecimalResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_bigdecimal.StringToBigDecimalResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_date_strategies.StringToDateResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies.DoubleToIntResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_integer_strategies.StringToIntResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.BooleanArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.ByteArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.CharArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.DoubleArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.FloatArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.IntArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.LongArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.ObjectArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_list_strategies.ShortArrayToListResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.BooleanToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.ByteToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.CharToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.DoubleToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.FloatToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.IntToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.LongToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.ObjectToStringResolveDependencyStrategy;
import info.smart_tools.smartactors.core.resolve_standard_types_strategies.to_string_strategies.ShortToStringResolveDependencyStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
                    .after("IOC")
                    .process(() -> {
                        try {
                            // to String strategies
                            IKey stringKey = Keys.getOrAdd(String.class.getCanonicalName() + "convert");
                            ResolveByTypeStrategy stringStrategy = new ResolveByTypeStrategy();

                            stringStrategy.register(Object.class,
                                    new ObjectToStringResolveDependencyStrategy());
                            stringStrategy.register(int.class,
                                    new IntToStringResolveDependencyStrategy());
                            stringStrategy.register(long.class,
                                    new LongToStringResolveDependencyStrategy());
                            stringStrategy.register(float.class,
                                    new FloatToStringResolveDependencyStrategy());
                            stringStrategy.register(double.class,
                                    new DoubleToStringResolveDependencyStrategy());
                            stringStrategy.register(boolean.class,
                                    new BooleanToStringResolveDependencyStrategy());
                            stringStrategy.register(byte.class,
                                    new ByteToStringResolveDependencyStrategy());
                            stringStrategy.register(short.class,
                                    new ShortToStringResolveDependencyStrategy());
                            stringStrategy.register(char.class,
                                    new CharToStringResolveDependencyStrategy());

                            IOC.register(stringKey, stringStrategy);

                            // to Integer strategies
                            IKey integerKey = Keys.getOrAdd(Integer.class.getCanonicalName() + "convert");
                            ResolveByTypeStrategy integerStrategy = new ResolveByTypeStrategy();

                            integerStrategy.register(String.class,
                                    new StringToIntResolveDependencyStrategy());
                            integerStrategy.register(Double.class,
                                    new DoubleToIntResolveDependencyStrategy());

                            IOC.register(integerKey, integerStrategy);

                            // to BigDecimal strategies
                            IKey bigDecimalKey = Keys.getOrAdd(BigDecimal.class.getCanonicalName() + "convert");
                            ResolveByTypeStrategy bigDecimalStrategy = new ResolveByTypeStrategy();

                            bigDecimalStrategy.register(String.class,
                                    new StringToBigDecimalResolveDependencyStrategy());
                            bigDecimalStrategy.register(Double.class,
                                    new DoubleToBigDecimalResolveDependencyStrategy());

                            IOC.register(bigDecimalKey, bigDecimalStrategy);

                            // to LocalDateTime strategies
                            IKey localDateTimeKey = Keys.getOrAdd(LocalDateTime.class.getCanonicalName() + "convert");
                            ResolveByTypeStrategy localDateTimeStrategy = new ResolveByTypeStrategy();

                            localDateTimeStrategy.register(String.class,
                                    new StringToDateResolveDependencyStrategy());

                            IOC.register(localDateTimeKey, localDateTimeStrategy);

                            // to list strategies
                            IKey listKey = Keys.getOrAdd(List.class.getCanonicalName() + "convert");
                            ResolveByTypeStrategy listStrategy = new ResolveByTypeStrategy();

                            listStrategy.register(boolean[].class, new BooleanArrayToListResolveDependencyStrategy());
                            listStrategy.register(byte[].class, new ByteArrayToListResolveDependencyStrategy());
                            listStrategy.register(char[].class, new CharArrayToListResolveDependencyStrategy());
                            listStrategy.register(double[].class, new DoubleArrayToListResolveDependencyStrategy());
                            listStrategy.register(float[].class, new FloatArrayToListResolveDependencyStrategy());
                            listStrategy.register(int[].class, new IntArrayToListResolveDependencyStrategy());
                            listStrategy.register(long[].class, new LongArrayToListResolveDependencyStrategy());
                            listStrategy.register(Object[].class, new ObjectArrayToListResolveDependencyStrategy());
                            listStrategy.register(short[].class, new ShortArrayToListResolveDependencyStrategy());

                            IOC.register(listKey, listStrategy);

                        } catch (RegistrationException | ResolutionException e) {
                            throw new RuntimeException(e);
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load ResolveStandardTypesStrategies plugin", e);
        }
    }
}
