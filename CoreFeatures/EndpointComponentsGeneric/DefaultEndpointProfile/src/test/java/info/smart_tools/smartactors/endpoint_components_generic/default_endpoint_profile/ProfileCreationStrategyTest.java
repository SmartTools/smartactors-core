package info.smart_tools.smartactors.endpoint_components_generic.default_endpoint_profile;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineCreationException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.exceptions.PipelineDescriptionNotFoundException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileCreationStrategyTest extends TrivialPluginsLoadingTestBase {
    private IEndpointProfile[] profileMocks;
    private IResolveDependencyStrategy namedProfileResolutionStrategy;
    private IEndpointPipelineSet pipelineSet;
    private IObject endpointConfig;
    private IEndpointPipeline pipelineMock = mock(IEndpointPipeline.class);

    @Override
    protected void registerMocks() throws Exception {
        profileMocks = new IEndpointProfile[] {
                mock(IEndpointProfile.class),
                mock(IEndpointProfile.class),
                mock(IEndpointProfile.class),
        };

        namedProfileResolutionStrategy = mock(IResolveDependencyStrategy.class);

        IOC.register(Keys.getOrAdd("endpoint profile"), namedProfileResolutionStrategy);

        pipelineSet = mock(IEndpointPipelineSet.class);
        endpointConfig = mock(IObject.class);
    }

    private static String conf1 = ("" +
            "{" +
            "   'extend':['1', '2']," +
            "   'pipelines':[" +
            "       {'id': '1'}," +
            "       {'id': '2'}" +
            "   ]" +
            "}").replace('\'','"');

    @Test public void Should_createProfile() throws Exception {
        IObject conf = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), conf1);
        when(namedProfileResolutionStrategy.resolve(eq("1"))).thenReturn(profileMocks[1]);
        when(namedProfileResolutionStrategy.resolve(eq("2"))).thenReturn(profileMocks[2]);

        when(profileMocks[1].createPipeline(eq("3"), any(), any())).thenThrow(PipelineDescriptionNotFoundException.class);
        when(profileMocks[2].createPipeline(eq("3"), any(), any())).thenReturn(pipelineMock);

        IEndpointProfile profile = new ProfileCreationStrategy().resolve(conf);

        try {
            profile.createPipeline("1", pipelineSet, endpointConfig);
            fail();
        } catch (PipelineCreationException e) { }

        IEndpointPipeline pipeline = profile.createPipeline("3", pipelineSet, endpointConfig);

        assertSame(pipelineMock, pipeline);
    }
}
