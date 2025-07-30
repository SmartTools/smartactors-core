package info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_standard_types_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ResolveStandardTypesStrategiesPluginTest extends IOCInitializer {

    private IBootstrap bootstrap;
    private ResolveStandardTypesStrategiesPlugin plugin;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void setUp() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.add(new BootstrapItem("IOC").process(()->{}));
        plugin = new ResolveStandardTypesStrategiesPlugin(bootstrap);
    }

    @Test
    public void ShouldRegisterStrategiesForStandardTypes() throws Exception {
        plugin.load();
        bootstrap.start();
        IKey stringKey = Keys.getKeyByName(String.class.getCanonicalName() + "convert");
        IKey expandableStrategyStringKey = Keys.getKeyByName("expandable_strategy#" + String.class.getCanonicalName());

        IKey characterKey = Keys.getKeyByName(Character.class.getCanonicalName() + "convert");
        IKey expandableStrategyCharacterKey = Keys.getKeyByName("expandable_strategy#" + Character.class.getCanonicalName());

        IKey booleanKey = Keys.getKeyByName(boolean.class.getCanonicalName() + "convert");
        IKey expandableStrategyBooleanKey = Keys.getKeyByName("expandable_strategy#" + boolean.class.getCanonicalName());

        IKey integerKey = Keys.getKeyByName(Integer.class.getCanonicalName() + "convert");
        IKey expandableStrategyIntegerKey = Keys.getKeyByName("expandable_strategy#" + Integer.class.getCanonicalName());

        IKey bigDecimalKey = Keys.getKeyByName(BigDecimal.class.getCanonicalName() + "convert");
        IKey expandableStrategyBigDecimalKey = Keys.getKeyByName("expandable_strategy#" + BigDecimal.class.getCanonicalName());

        IKey localDateTimeKey = Keys.getKeyByName(LocalDateTime.class.getCanonicalName() + "convert");
        IKey expandableStrategyLocalDateTimeKey = Keys.getKeyByName("expandable_strategy#" + LocalDateTime.class.getCanonicalName());

        IKey intKey = Keys.getKeyByName(int.class.getCanonicalName() + "convert");
        IKey expandableStrategyIntKey = Keys.getKeyByName("expandable_strategy#" + int.class.getCanonicalName());

        IKey listKey = Keys.getKeyByName(List.class.getCanonicalName() + "convert");
        IKey expandableStrategyListKey = Keys.getKeyByName("expandable_strategy#" + List.class.getCanonicalName());

        IKey lngKey = Keys.getKeyByName(long.class.getCanonicalName() + "convert");
        IKey expandableStrategyLngKey = Keys.getKeyByName("expandable_strategy#" + long.class.getCanonicalName());

        IKey longKey = Keys.getKeyByName(Long.class.getCanonicalName() + "convert");
        IKey expandableStrategyLongKey = Keys.getKeyByName("expandable_strategy#" + Long.class.getCanonicalName());

        Object a1 = IOC.resolve(stringKey, 1);
        Object aExp1 = IOC.resolve(expandableStrategyStringKey);

        Object a2 = IOC.resolve(characterKey, "111");
        Object aExp2 = IOC.resolve(expandableStrategyCharacterKey);

        Object a3 = IOC.resolve(booleanKey, true);
        Object aExp3 = IOC.resolve(expandableStrategyBooleanKey);

        Object a4 = IOC.resolve(integerKey, 1);
        Object aExp4 = IOC.resolve(expandableStrategyIntegerKey);

        Object a5 = IOC.resolve(bigDecimalKey, 1);
        Object aExp5 = IOC.resolve(expandableStrategyBigDecimalKey);

        Object a6 = IOC.resolve(localDateTimeKey, "2015-08-04T10:11:30");
        Object aExp6 = IOC.resolve(expandableStrategyLocalDateTimeKey);

        Object a7 = IOC.resolve(intKey, "1");
        Object aExp7 = IOC.resolve(expandableStrategyIntKey);

        Object a8 = IOC.resolve(listKey, new boolean[] {true, false});
        Object aExp8 = IOC.resolve(expandableStrategyListKey);

        Object a9 = IOC.resolve(lngKey, 1);
        Object aExp9 = IOC.resolve(expandableStrategyLngKey);

        Object a10 = IOC.resolve(longKey, 1);
        Object aExp10 = IOC.resolve(expandableStrategyLongKey);

        assertNotNull(a1);
        assertNotNull(aExp1);
        assertNotNull(a2);
        assertNotNull(aExp2);
        assertNotNull(a3);
        assertNotNull(aExp3);
        assertNotNull(a4);
        assertNotNull(aExp4);
        assertNotNull(a5);
        assertNotNull(aExp5);
        assertNotNull(a6);
        assertNotNull(aExp6);
        assertNotNull(a7);
        assertNotNull(aExp7);
        assertNotNull(a8);
        assertNotNull(aExp8);
        assertNotNull(a9);
        assertNotNull(aExp9);
        assertNotNull(a10);
        assertNotNull(aExp10);

        bootstrap.revert();
        try {
            Object a11 = IOC.resolve(stringKey, 1);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp11 = IOC.resolve(expandableStrategyStringKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a21 = IOC.resolve(characterKey, "111");
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp21 = IOC.resolve(expandableStrategyCharacterKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a31 = IOC.resolve(booleanKey, true);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp31 = IOC.resolve(expandableStrategyBooleanKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a41 = IOC.resolve(integerKey, 1);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp41 = IOC.resolve(expandableStrategyIntegerKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a51 = IOC.resolve(bigDecimalKey, 1);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp51 = IOC.resolve(expandableStrategyBigDecimalKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a61 = IOC.resolve(localDateTimeKey, "2015-08-04T10:11:30");
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp61 = IOC.resolve(expandableStrategyLocalDateTimeKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a71 = IOC.resolve(intKey, "1");
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp71 = IOC.resolve(expandableStrategyIntKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a81 = IOC.resolve(listKey, new boolean[] {true, false});
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp81 = IOC.resolve(expandableStrategyListKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a91 = IOC.resolve(lngKey, new boolean[] {true, false});
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp91 = IOC.resolve(expandableStrategyListKey);
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object a101 = IOC.resolve(longKey, new boolean[] {true, false});
            fail();
        } catch (ResolutionException e) {/**/}
        try {
            Object aExp101 = IOC.resolve(expandableStrategyListKey);
            fail();
        } catch (ResolutionException e) {/**/}
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_InternalErrorIsOccurred() throws Exception {
        IBootstrap b = mock(IBootstrap.class);
        IPlugin pl = new ResolveStandardTypesStrategiesPlugin(b);
        doThrow(InvalidArgumentException.class).when(b).add(any());
        pl.load();
    }

    @Test(expected = ProcessExecutionException.class)
    public void ShouldThrowException_When_ExceptionInLambdaIsThrown() throws Exception {
        plugin.load();
        ScopeProvider.setCurrentScope(null);
        bootstrap.start();
    }
}
