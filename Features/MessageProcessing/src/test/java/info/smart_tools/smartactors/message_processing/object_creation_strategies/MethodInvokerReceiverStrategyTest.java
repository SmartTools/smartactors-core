package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link MethodInvokerReceiverStrategy}.
 */
public class MethodInvokerReceiverStrategyTest extends IOCInitializer {
    public class AObject {
        public void method1(Object o) {
        }

        public void method2(Object o, Object o2) {
        }
    }

    private AObject object = new AObject();

    private IStrategy strategy;

    private IObject configMock;
    private IReceiverGenerator receiverGeneratorMock;
    private IStrategy defaultWrapperResolutionStrategyMock;
    private IStrategy specialWrapperResolutionStrategyMock;
    private IStrategy defaultWrapperResolutionStrategyResolutionStrategyMock;
    private IStrategy specialWrapperResolutionStrategyResolutionStrategyMock;
    private IMessageReceiver receiverMock1;
    private IMessageReceiver receiverMock2;

    @Override
    protected void registerMocks() throws Exception {
        receiverGeneratorMock = mock(IReceiverGenerator.class);

        IOC.register(Keys.getKeyByName(IReceiverGenerator.class.getCanonicalName()), new SingletonStrategy(receiverGeneratorMock));

        configMock = mock(IObject.class);

        strategy = new MethodInvokerReceiverStrategy();

        defaultWrapperResolutionStrategyMock = mock(IStrategy.class);
        specialWrapperResolutionStrategyMock = mock(IStrategy.class);
        defaultWrapperResolutionStrategyResolutionStrategyMock = mock(IStrategy.class);
        specialWrapperResolutionStrategyResolutionStrategyMock = mock(IStrategy.class);
        receiverMock1 = mock(IMessageReceiver.class);
        receiverMock2 = mock(IMessageReceiver.class);

        when(defaultWrapperResolutionStrategyResolutionStrategyMock.resolve(same(Object.class)))
                .thenReturn(defaultWrapperResolutionStrategyMock);
        when(specialWrapperResolutionStrategyResolutionStrategyMock.resolve(same(Object.class)))
                .thenReturn(specialWrapperResolutionStrategyMock);

        when(receiverGeneratorMock.generate(same(object), same(defaultWrapperResolutionStrategyMock), eq("method1")))
                .thenReturn(receiverMock1);
        when(receiverGeneratorMock.generate(same(object), same(specialWrapperResolutionStrategyMock), eq("method1")))
                .thenReturn(receiverMock2);

        IOC.register(Keys.getKeyByName("default wrapper resolution strategy dependency for invoker receiver"),
                defaultWrapperResolutionStrategyResolutionStrategyMock);
        IOC.register(Keys.getKeyByName("special wrapper resolution strategy dependency for invoker receiver"),
                specialWrapperResolutionStrategyResolutionStrategyMock);
    }

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Test(expected = StrategyException.class)
    public void Should_throwWhenMethodHasTooMuchArguments()
            throws Exception {
        strategy.resolve(
                object,
                object.getClass().getMethod("method2", Object.class, Object.class),
                configMock
        );
    }

    @Test
    public void Should_generateReceiverWithDefaultWrapperStrategy()
            throws Exception {
        assertSame(receiverMock1, strategy.resolve(
                object,
                object.getClass().getMethod("method1", Object.class),
                configMock
        ));
    }

    @Test
    public void Should_generateReceiverWithSpecificWrapperStrategy()
            throws Exception {
        when(configMock.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "wrapperResolutionStrategyDependency")))
                .thenReturn("special wrapper resolution strategy dependency for invoker receiver");

        assertSame(receiverMock2, strategy.resolve(
                object,
                object.getClass().getMethod("method1", Object.class),
                configMock
        ));
    }
}
