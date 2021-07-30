package info.smart_tools.smartactors.message_processing.receiver_chain;

import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.module_able.IModuleAble;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_able.IScopeAble;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Tests for {@link ImmutableReceiverChainStrategy}.
 */
@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class ImmutableReceiverChainStrategyTest {
    private IKey keyStorageKey;
    private IKey fieldNameKey;
    private IKey iobjectKey;
    private IKey receiverIdKey;
    private IKey chainIdKey;

    private IChainStorage chainStorageMock;
    private IRouter routerMock;
    private IObject chainAndEnv;

    @Before
    public void setUp()
            throws Exception {

        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        keyStorageKey = mock(IKey.class);
        fieldNameKey = mock(IKey.class);
        iobjectKey = mock(IKey.class);
        receiverIdKey = mock(IKey.class);
        chainIdKey = mock(IKey.class);

        chainStorageMock = mock(IChainStorage.class);
        routerMock = mock(IRouter.class);
        this.chainAndEnv = mock(IObject.class);

        mockStatic(IOC.class);

        when(IOC.getKeyForKeyByNameStrategy()).thenReturn(keyStorageKey);
        when(IOC.resolve(keyStorageKey, "info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(fieldNameKey);
        when(IOC.resolve(keyStorageKey, "chain_id_from_map_name")).thenReturn(chainIdKey);
        when(IOC.resolve(keyStorageKey, "receiver_id_from_iobject")).thenReturn(receiverIdKey);
        when(IOC.resolve(keyStorageKey, "info.smart_tools.smartactors.iobject.iobject.IObject")).thenReturn(iobjectKey);

        when(IOC.resolve(fieldNameKey, "steps")).thenReturn(mock(IFieldName.class));
        when(IOC.resolve(fieldNameKey, "exceptional")).thenReturn(mock(IFieldName.class));
        when(IOC.resolve(fieldNameKey, "class")).thenReturn(mock(IFieldName.class));
        when(IOC.resolve(fieldNameKey, "chain")).thenReturn(mock(IFieldName.class));
        when(IOC.resolve(iobjectKey)).thenReturn(this.chainAndEnv);
    }

    @Test
    public void Should_createImmutableReceiverChains()
            throws Exception {
        IObject description = mock(IObject.class);
        List<IObject> steps = new LinkedList<>();
        List<IObject> exceptionals = new LinkedList<>();
        Object chainId = "chain";
        Object chainName = "chainName";

        IReceiverChain exceptionalChain1 = mock(IReceiverChain.class);
        IReceiverChain exceptionalChain2 = mock(IReceiverChain.class);

        IMessageReceiver receiver1 = mock(IMessageReceiver.class);
        IMessageReceiver receiver2 = mock(IMessageReceiver.class);

        IScope scope = mock(IScope.class);
        IModule module = ModuleManager.getCurrentModule();

        IObject step1 = mock(IObject.class);
        IObject step2 = mock(IObject.class);

        when(description.getValue(same(IOC.resolve(fieldNameKey, "steps")))).thenReturn(steps);
        when(description.getValue(same(IOC.resolve(fieldNameKey, "exceptional")))).thenReturn(exceptionals);
        when(description.getValue(same(IOC.resolve(fieldNameKey, "id")))).thenReturn(chainName);

        steps.add(step1);
        steps.add(step2);

        IObject exceptional1 = mock(IObject.class);

        when(exceptional1.getValue(same(IOC.resolve(fieldNameKey, "class")))).thenReturn(Exception.class.getCanonicalName());
        when(exceptional1.getValue(same(IOC.resolve(fieldNameKey, "chain")))).thenReturn("ex_ch1");

        IObject exceptional2 = mock(IObject.class);

        when(exceptional2.getValue(same(IOC.resolve(fieldNameKey, "class")))).thenReturn(Error.class.getCanonicalName());
        when(exceptional2.getValue(same(IOC.resolve(fieldNameKey, "chain")))).thenReturn("ex_ch2");

        when(chainStorageMock.resolve("ex_ch_1id")).thenReturn(exceptionalChain1);
        when(chainStorageMock.resolve("ex_ch_2id")).thenReturn(exceptionalChain2);

        exceptionals.add(exceptional1);
        exceptionals.add(exceptional2);

        when(IOC.resolve(same(chainIdKey), eq("ex_ch1"))).thenReturn("ex_ch_1id");
        when(IOC.resolve(same(chainIdKey), eq("ex_ch2"))).thenReturn("ex_ch_2id");

        when(IOC.resolve(same(receiverIdKey), same(step1))).thenReturn("rec1");
        when(IOC.resolve(same(receiverIdKey), same(step2))).thenReturn("rec2");

        when(routerMock.route("rec1")).thenReturn(receiver1);
        when(routerMock.route("rec2")).thenReturn(receiver2);

        IReceiverChain chain = new ImmutableReceiverChainStrategy().resolve(chainId, description, routerMock, scope, module);

        assertNotNull(chain);
        assertSame(chain.getName(), chainName);
        assertSame(((IScopeAble) chain).getScope(), scope);
        assertSame(((IModuleAble) chain).getModule(), module);
    }

    @Test(expected = StrategyException.class)
    public void Should_wrapExceptions()
            throws Exception {
        IObject description = mock(IObject.class);

        when(description.getValue(any())).thenThrow(ReadValueException.class);

        new ImmutableReceiverChainStrategy().resolve("chain", description, routerMock, null, null);
    }
}
