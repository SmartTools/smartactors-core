package info.smart_tools.smartactors.http_endpoint_plugins.get_header_from_request_rule_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint.strategy.get_header_from_request.GetHeaderFromRequestRule;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, GetHeaderFromRequestRulePlugin.class, ApplyFunctionToArgumentsStrategy.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class GetHeaderFromRequestRulePluginTest {
    private GetHeaderFromRequestRulePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new GetHeaderFromRequestRulePlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

//        verify(bootstrapItem).after("IOC");
//        verify(bootstrapItem).after("wds_object");
//        verify(bootstrapItem).before("starter");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey strategyKey = mock(IKey.class);
        when(Keys.getKeyByName(IStrategy.class.getCanonicalName())).thenReturn(strategyKey);

        GetHeaderFromRequestRule targetObject = mock(GetHeaderFromRequestRule.class);
        whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(targetObject);


        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(IStrategy.class.getCanonicalName());

        verifyNew(GetHeaderFromRequestRule.class).withNoArguments();

        verifyStatic();
        IOC.resolve(strategyKey, "getHeaderFromRequestRule", targetObject);
        /*IKey ruleKey = mock(IKey.class);
        when(Keys.getKeyByName(GetHeaderFromRequestRule.class.getCanonicalName())).thenReturn(ruleKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName(GetHeaderFromRequestRule.class.getCanonicalName());

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> createNewInstanceStrategyArgumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);

        verifyStatic();
        IOC.register(eq(ruleKey), createNewInstanceStrategyArgumentCaptor.capture());

        IObject arg = mock(IObject.class);

        GetHeaderFromRequestRule rule = mock(GetHeaderFromRequestRule.class);
        whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(rule);

        assertTrue("Objects must return correct object", createNewInstanceStrategyArgumentCaptor.getValue().resolve(arg) == rule);

        verifyNew(GetHeaderFromRequestRule.class).withNoArguments();*/
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

//        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        when(Keys.getKeyByName(IStrategy.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        GetHeaderFromRequestRule targetObject = mock(GetHeaderFromRequestRule.class);
        whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(targetObject);

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {
            verifyStatic();
            Keys.getKeyByName(IStrategy.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IActionNoArgs> actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

//        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey strategyKey = mock(IKey.class);
        when(Keys.getKeyByName(IStrategy.class.getCanonicalName())).thenReturn(strategyKey);

        GetHeaderFromRequestRule targetObject = mock(GetHeaderFromRequestRule.class);
        whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(targetObject);

        when(IOC.resolve(strategyKey, "getHeaderFromRequestRule", targetObject)).thenThrow(new ResolutionException(""));

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecutionException e) {
            verifyStatic();
            Keys.getKeyByName(IStrategy.class.getCanonicalName());

            verifyNew(GetHeaderFromRequestRule.class).withNoArguments();

            verifyStatic();
            IOC.resolve(strategyKey, "getHeaderFromRequestRule", targetObject);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}
