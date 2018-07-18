package info.smart_tools.smartactors.endpoint_components_generic.endpoints_config_section_strategy;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link EndpointsConfigSectionStrategy}.
 */
public class EndpointsConfigSectionStrategyTest extends TrivialPluginsLoadingTestBase {
    IEndpointProfile profileMock;
    IEndpointPipelineSet pipelineSetMock;
    IResolveDependencyStrategy profileStrategy, pipelineSetStrategy, endpointStrategy;

    @Override
    protected void registerMocks() throws Exception {
        profileMock = mock(IEndpointProfile.class);
        pipelineSetMock = mock(IEndpointPipelineSet.class);
        profileStrategy = mock(IResolveDependencyStrategy.class);
        pipelineSetStrategy = mock(IResolveDependencyStrategy.class);
        endpointStrategy = mock(IResolveDependencyStrategy.class);

        IOC.register(Keys.getOrAdd("endpoint profile"), profileStrategy);
        IOC.register(Keys.getOrAdd("create endpoint pipeline set"), pipelineSetStrategy);
        IOC.register(Keys.getOrAdd("create endpoint"), endpointStrategy);

        when(profileStrategy.resolve(eq("profile-1"))).thenReturn(profileMock);
        when(pipelineSetStrategy.resolve(same(profileMock), any())).thenReturn(pipelineSetMock);
        when(endpointStrategy.resolve(eq("skeleton-1"), any(), same(pipelineSetMock))).thenReturn(new Object());
    }

    @Test public void Should_parseSection() throws Exception {
        String confS = (
                "{" +
                "   'endpoints': [" +
                "       {" +
                "           'skeleton': 'skeleton-1'," +
                "           'profile': 'profile-1'" +
                "       }" +
                "   ]" +
                "}").replace('\'','"');
        IObject conf = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), confS);

        new EndpointsConfigSectionStrategy().onLoadConfig(conf);

        verify(endpointStrategy).resolve(eq("skeleton-1"),
                same(((List) conf.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"))).get(0)),
                same(pipelineSetMock));
    }

    @Test public void Should_useCorrectSectionName() throws Exception {
        assertEquals(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "endpoints"),
                new EndpointsConfigSectionStrategy().getSectionName());
    }
}
