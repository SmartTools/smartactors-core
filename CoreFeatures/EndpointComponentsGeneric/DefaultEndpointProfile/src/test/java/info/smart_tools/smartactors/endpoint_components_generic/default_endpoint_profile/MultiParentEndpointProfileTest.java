package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MultiParentEndpointProfileTest {
    private List<IEndpointProfile> parentMocks;
    private IEndpointPipelineSet pipelineSetMock;
    private IObject endpointConfig;
    private IEndpointPipeline pipelineMock;

    @Before public void setUp() throws Exception {
        parentMocks = Arrays.asList(mock(IEndpointProfile.class), mock(IEndpointProfile.class));
        endpointConfig = mock(IObject.class);
        pipelineSetMock = mock(IEndpointPipelineSet.class);
        pipelineMock = mock(IEndpointPipeline.class);
    }

    @Test(expected = PipelineDescriptionNotFoundException.class)
    public void Should_throwWhenNoParentsDefined() throws Exception {
        new MultiParentEndpointProfile(Collections.emptyList())
                .createPipeline("1", pipelineSetMock, endpointConfig);
    }

    @Test public void Should_tryToUseAllParentsToResolvePipeline() throws Exception {
        when(parentMocks.get(0).createPipeline(any(), any(), any()))
                .thenThrow(PipelineDescriptionNotFoundException.class);
        when(parentMocks.get(1).createPipeline(any(), any(), any()))
                .thenReturn(pipelineMock);

        IEndpointPipeline pipeline = new MultiParentEndpointProfile(parentMocks)
                .createPipeline("1", pipelineSetMock, endpointConfig);

        assertSame(pipelineMock, pipeline);
        verify(parentMocks.get(1)).createPipeline(eq("1"), same(pipelineSetMock), same(endpointConfig));
    }

    @Test(expected = PipelineDescriptionNotFoundException.class)
    public void Should_throwWhenNoParensProvideRequiredPipeline() throws Exception {
        when(parentMocks.get(0).createPipeline(any(), any(), any()))
                .thenThrow(PipelineDescriptionNotFoundException.class);
        when(parentMocks.get(1).createPipeline(any(), any(), any()))
                .thenThrow(PipelineDescriptionNotFoundException.class);

        new MultiParentEndpointProfile(parentMocks)
                .createPipeline("1", pipelineSetMock, endpointConfig);
    }
}
