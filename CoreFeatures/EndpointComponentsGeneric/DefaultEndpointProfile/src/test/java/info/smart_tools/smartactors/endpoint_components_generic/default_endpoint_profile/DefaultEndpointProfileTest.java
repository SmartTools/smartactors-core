package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class DefaultEndpointProfileTest extends TrivialPluginsLoadingTestBase {
    private IEndpointPipelineSet pipelineSetMock;
    private IObject endpointConfig, pipelineDesc;
    private IResolveDependencyStrategy pipelineStrategy;
    private IEndpointProfile parentMock;
    private IEndpointPipeline pipelineMock;

    @Override
    protected void registerMocks() throws Exception {
        pipelineSetMock = mock(IEndpointPipelineSet.class);
        endpointConfig = mock(IObject.class);
        pipelineDesc = mock(IObject.class);
        pipelineStrategy = mock(IResolveDependencyStrategy.class);
        parentMock = mock(IEndpointProfile.class);
        pipelineMock = mock(IEndpointPipeline.class);

        IOC.register(Keys.getOrAdd("create endpoint pipeline"), pipelineStrategy);
    }

    @Test public void Should_resolvePipelineWhenDescriptionIsPresent() throws Exception {
        IEndpointProfile profile = new DefaultEndpointProfile(parentMock, new HashMap<String, IObject>() {{
            put("1", pipelineDesc);
        }});

        when(pipelineStrategy.resolve(anyVararg())).thenReturn(pipelineMock);

        IEndpointPipeline pipeline = profile.createPipeline("1", pipelineSetMock, endpointConfig);

        assertSame(pipelineMock, pipeline);
        verify(pipelineStrategy).resolve(same(pipelineDesc), same(endpointConfig), same(pipelineSetMock));
        verifyZeroInteractions(parentMock);
    }

    @Test public void Should_delegatePipelineCreationToParentWhenNoDescriptionIsAvailable() throws Exception {
        IEndpointProfile profile = new DefaultEndpointProfile(parentMock, new HashMap<String, IObject>() {{
            put("1", pipelineDesc);
        }});

        when(parentMock.createPipeline(anyString(), any(), any())).thenReturn(pipelineMock);

        IEndpointPipeline pipeline = profile.createPipeline("2", pipelineSetMock, endpointConfig);

        assertSame(pipelineMock, pipeline);
        verify(parentMock).createPipeline(eq("2"), same(pipelineSetMock), same(endpointConfig));
        verifyZeroInteractions(pipelineStrategy);
    }

    @Test(expected = PipelineCreationException.class)
    public void Should_wrapExceptionOccuredOnResolution() throws Exception {
        IEndpointProfile profile = new DefaultEndpointProfile(parentMock, new HashMap<String, IObject>() {{
            put("1", pipelineDesc);
        }});

        when(pipelineStrategy.resolve(anyVararg())).thenThrow(ResolveDependencyStrategyException.class);

        profile.createPipeline("1", pipelineSetMock, endpointConfig);
    }
}
