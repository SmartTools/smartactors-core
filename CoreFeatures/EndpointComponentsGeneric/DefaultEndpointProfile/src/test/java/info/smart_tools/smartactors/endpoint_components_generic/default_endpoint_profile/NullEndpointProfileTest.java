package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class NullEndpointProfileTest {
    @Test(expected = PipelineDescriptionNotFoundException.class)
    public void Should_throwException() throws Exception {
        NullEndpointProfile.INSTANCE.createPipeline("id", mock(IEndpointPipelineSet.class), mock(IObject.class));
    }
}
