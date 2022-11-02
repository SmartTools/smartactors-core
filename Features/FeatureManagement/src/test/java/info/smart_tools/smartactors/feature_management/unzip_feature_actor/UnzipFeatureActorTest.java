package info.smart_tools.smartactors.feature_management.unzip_feature_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.exception.UnzipFeatureException;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.wrapper.UnzipFeatureWrapper;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by sevenbits on 12/19/16.
 */
public class UnzipFeatureActorTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Test
    public void checkCreation()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        assertNotNull(actor);
    }

    @Test
    public void checkUnzipMethod()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        UnzipFeatureWrapper wrapper = mock(UnzipFeatureWrapper.class);
        IFeature feature = mock(IFeature.class);
        when(feature.getName()).thenReturn("test-feature");
        when(feature.getDependencies()).thenReturn(null);
        when(feature.getGroupId()).thenReturn("com.groupId");
        String fileName = "target" + File.separator + "test-classes" + File.separator + "test-feature-VERSION.zip";
        String directory = "target" + File.separator + "test-classes" + File.separator + "test-feature-VERSION";
        when(feature.getLocation()).thenReturn(
                new Path(fileName)
        );
        when(wrapper.getFeature()).thenReturn(feature);
        actor.unzip(wrapper);
        verify(feature, times(1)).setDependencies(
                new HashSet<String>(){{
                    add("info.smart_tools.smartactors:first-test-feature");
                    add("info.smart_tools.smartactors:second-test-feature");
                }}
        );
        verify(feature, times(1)).setLocation(new Path(directory));
    }

    @Test (expected = UnzipFeatureException.class)
    public void checkUnzipFeatureException()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        UnzipFeatureWrapper wrapper = mock(UnzipFeatureWrapper.class);
        when(wrapper.getFeature()).thenThrow(ReadValueException.class);
        actor.unzip(wrapper);
        fail();
    }

    @Test
    public void checkUnzipOnUnsupportedFeatureFormat()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        UnzipFeatureWrapper wrapper = mock(UnzipFeatureWrapper.class);
        IFeature feature = mock(IFeature.class);
        when(feature.getName()).thenReturn("test-feature");
        when(feature.getDependencies()).thenReturn(null);
        String fileName = "target/test-classes/unsupported-test-feature-VERSION.zip";
        when(feature.getLocation()).thenReturn(
                new Path(fileName)
        );
        when(wrapper.getFeature()).thenReturn(feature);
        actor.unzip(wrapper);
        verify(feature, times(1)).setFailed(true);
    }

    @Test
    public void checkUnzipOnBrokenFeatureFormat()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        UnzipFeatureWrapper wrapper = mock(UnzipFeatureWrapper.class);
        IFeature feature = mock(IFeature.class);
        when(feature.getName()).thenReturn("test-feature");
        when(feature.getDependencies()).thenReturn(null);
        String fileName = "target/test-classes/broken-test-feature-VERSION.zip";
        when(feature.getLocation()).thenReturn(
                new Path(fileName)
        );
        when(wrapper.getFeature()).thenReturn(feature);
        actor.unzip(wrapper);
        verify(feature, times(1)).setFailed(true);
    }
}

