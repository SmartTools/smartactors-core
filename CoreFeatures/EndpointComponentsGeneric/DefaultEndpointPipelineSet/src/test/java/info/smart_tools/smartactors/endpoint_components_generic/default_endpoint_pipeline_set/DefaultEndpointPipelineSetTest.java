package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_pipeline_set;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class DefaultEndpointPipelineSetTest {
    private IEndpointProfile endpointProfile;
    private IObject endpointConfig;
    private IEndpointPipeline[] pipelineMocks = {
            mock(IEndpointPipeline.class),
            mock(IEndpointPipeline.class),
            mock(IEndpointPipeline.class),
    };

    @Before public void setUp() throws Exception {
        endpointProfile = mock(IEndpointProfile.class);
        endpointConfig = mock(IObject.class);
    }

    @Test public void Should_resolvePipelines() throws Exception {
        when(endpointProfile.createPipeline(eq("1"), any(), any())).thenAnswer(invocationOnMock -> {
            IEndpointPipeline pipeline = invocationOnMock.getArgumentAt(1, IEndpointPipelineSet.class).getPipeline("2");
            assertSame(pipelineMocks[2], pipeline);
            return pipelineMocks[1];
        });
        when(endpointProfile.createPipeline(eq("2"), any(), any())).thenReturn(pipelineMocks[2]);

        IEndpointPipelineSet pipelineSet = new DefaultEndpointPipelineSet(endpointConfig, endpointProfile);

        IEndpointPipeline pipeline = pipelineSet.getPipeline("1");

        assertSame(pipelineMocks[1], pipeline);

        verify(endpointProfile).createPipeline(eq("1"), same(pipelineSet), same(endpointConfig));
        verify(endpointProfile).createPipeline(eq("2"), same(pipelineSet), same(endpointConfig));
        verifyNoMoreInteractions(endpointProfile);
    }

    @Test(expected = PipelineCreationException.class)
    public void Should_detectPipelineDependencyLoops() throws Exception {
        when(endpointProfile.createPipeline(eq("1"), any(), any())).thenAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(1, IEndpointPipelineSet.class).getPipeline("2");
            fail();
            return null;
        });
        when(endpointProfile.createPipeline(eq("2"), any(), any())).thenAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(1, IEndpointPipelineSet.class).getPipeline("1");
            fail();
            return null;
        });

        new DefaultEndpointPipelineSet(endpointConfig, endpointProfile).getPipeline("1");
    }
}
