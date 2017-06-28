package info.smart_tools.smartactors.feature_management.unzip_feature_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.exception.UnzipFeatureException;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.wrapper.UnzipFeatureWrapper;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by sevenbits on 12/19/16.
 */
public class UnzipFeatureActorTest {

    private IStrategyContainer container = new StrategyContainer();

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), this.container);
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );

        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), IFieldName.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (Exception e) {
                                throw new RuntimeException("exception", e);
                            }
                        }
                )
        );
        IOC.register(Keys.getOrAdd(IObject.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(args -> {
                    if (args.length == 0) {
                        return new DSObject();
                    } else if (args.length == 1 && args[0] instanceof String) {
                        try {
                            return new DSObject((String) args[0]);
                        } catch (InvalidArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        throw new RuntimeException("Invalid arguments for IObject creation.");
                    }
                }));
    }

    @Test
    public void checkCreation()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        assertNotNull(actor);
    }

    @Ignore
    @Test
    public void checkUnzipMethod()
            throws Exception {
        UnzipFeatureActor actor = new UnzipFeatureActor();
        UnzipFeatureWrapper wrapper = mock(UnzipFeatureWrapper.class);
        IFeature feature = mock(IFeature.class);
        when(feature.getName()).thenReturn("test-feature");
        when(feature.getDependencies()).thenReturn(null);
        String fileName = "target/test-classes/test-feature-VERSION-archive.zip";
        String directory = "target/test-classes/test-feature-VERSION";
        when(feature.getFeatureLocation()).thenReturn(
                new Path(fileName)
        );
        when(wrapper.getFeature()).thenReturn(feature);
        actor.unzip(wrapper);
        verify(feature, times(1)).setDependencies(
                new HashSet<String>(){{add("first-test-feature"); add("second-test-feature");}}
        );
        verify(feature, times(1)).setFeatureLocation(new Path(directory));
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
        String fileName = "target/test-classes/unsupported-test-feature-VERSION-archive.zip";
        when(feature.getFeatureLocation()).thenReturn(
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
        String fileName = "target/test-classes/broken-test-feature-VERSION-archive.zip";
        when(feature.getFeatureLocation()).thenReturn(
                new Path(fileName)
        );
        when(wrapper.getFeature()).thenReturn(feature);
        actor.unzip(wrapper);
        verify(feature, times(1)).setFailed(true);
    }
}

