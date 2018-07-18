package info.smart_tools.smartactors.endpoint_components_generic.endpoint_profiles_configuration_section;

import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_profile.IEndpointProfile;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndpointProfilesConfigurationSectionStrategyTest extends TrivialPluginsLoadingTestBase {
    private IAdditionDependencyStrategy namedProfilesStrategy;
    private IResolveDependencyStrategy creationStrategy;
    private IEndpointProfile profileMock = mock(IEndpointProfile.class);

    @Override
    protected void registerMocks() throws Exception {
        namedProfilesStrategy = mock(IAdditionDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("expandable_strategy#endpoint profile"),
                new SingletonStrategy(namedProfilesStrategy));
        creationStrategy = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("parse endpoint profile"), creationStrategy);
    }

    private static String conf1 = ("" +
            "{" +
            "   'endpointProfiles': [" +
            "       {" +
            "           'id': '1'" +
            "       }" +
            "   ]" +
            "}").replace('\'','"');

    @Test public void Should_createProfiles() throws Exception {
        when(creationStrategy.resolve(any()))
                .thenReturn(profileMock).thenThrow(ResolveDependencyStrategyException.class);
        new EndpointProfilesConfigurationSectionStrategy().onLoadConfig(
                IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), conf1)
        );

        ArgumentCaptor<IResolveDependencyStrategy> resolveDependencyStrategyArgumentCaptor = ArgumentCaptor.forClass(IResolveDependencyStrategy.class);

        verify(namedProfilesStrategy).register(eq("1"), resolveDependencyStrategyArgumentCaptor.capture());
        assertSame(profileMock, resolveDependencyStrategyArgumentCaptor.getValue().resolve());
    }
}
