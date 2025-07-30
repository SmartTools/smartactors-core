package info.smart_tools.smartactors.checkpoint.failure_action;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link CheckpointFailureActionSectionStrategy}.
 */
public class CheckpointFailureActionSectionStrategyTest extends IOCInitializer {
    private IStrategy actionStrategyMock;
    private IStrategy defaultActionStrategyMock;
    private IAction<IObject> actionMock;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        actionStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("that checkpoint failure action"), actionStrategyMock);

        defaultActionStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("default configurable checkpoint failure action"), defaultActionStrategyMock);

        actionMock = mock(IAction.class);
    }

    @Test
    public void Should_returnSectionName()
            throws Exception {
        assertEquals(
                IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpoint_failure_action"),
                new CheckpointFailureActionSectionStrategy().getSectionName()
        );
    }

    @Test
    public void Should_resolveAndRegisterCustomAction()
            throws Exception {
        IObject config = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'checkpoint_failure_action':{'action':'that checkpoint failure action'}}".replace('\'','"'));
        when(actionStrategyMock.resolve(
                same(config.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpoint_failure_action")))
        )).thenReturn(actionMock);

        new CheckpointFailureActionSectionStrategy().onLoadConfig(config);

        assertSame(actionMock, IOC.resolve(Keys.getKeyByName("checkpoint failure action")));
    }

    @Test
    public void Should_resolveAndRegisterDefaultActionIfActionDependencyNameWasNotGiven()
            throws Exception {
        IObject config = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'checkpoint_failure_action':{}}".replace('\'','"'));
        when(defaultActionStrategyMock.resolve(
                same(config.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpoint_failure_action")))
        )).thenReturn(actionMock);

        CheckpointFailureActionSectionStrategy sectionStrategy = new CheckpointFailureActionSectionStrategy();

        sectionStrategy.onLoadConfig(config);
        assertSame(actionMock, IOC.resolve(Keys.getKeyByName("checkpoint failure action")));

        sectionStrategy.onRevertConfig(config);
        try {
            IOC.resolve(Keys.getKeyByName("checkpoint failure action"));
            fail();
        } catch (ResolutionException e) { }

        sectionStrategy.getSectionName();
    }
}
