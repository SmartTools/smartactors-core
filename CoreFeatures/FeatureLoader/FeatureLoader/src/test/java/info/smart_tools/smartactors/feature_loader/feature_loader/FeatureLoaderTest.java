package info.smart_tools.smartactors.feature_loader.feature_loader;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.IFeatureStatus;
import info.smart_tools.smartactors.feature_loader.interfaces.ifilesystem_facade.IFilesystemFacade;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Test for {@link FeatureLoader}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class FeatureLoaderTest {
    private IKey fieldNameKey = mock(IKey.class);
    private IFieldName featureNameFN = mock(IFieldName.class);
    private IFieldName afterFeaturesFN = mock(IFieldName.class);

    private IKey pluginCreatorKey = mock(IKey.class);
    private IPluginCreator pluginCreatorMock;

    private IKey pluginLoaderVisitorKey = mock(IKey.class);
    private IPluginLoaderVisitor pluginLoaderVisitorMock;

    private IKey configurationManagerKey = mock(IKey.class);
    private IConfigurationManager configurationManagerMock;

    private IKey filesystemFacadeKey = mock(IKey.class);
    private IFilesystemFacade filesystemFacadeMock;

    private IKey iobjectKey = mock(IKey.class);
    private IObject metafeatureConfigMock = mock(IObject.class);

    private IKey featureStatusKey = mock(IKey.class);
    private FeatureStatusImpl featureStatusMock1;
    private FeatureStatusImpl featureStatusMock2;

    private IKey configurationObjectKey = mock(IKey.class);
    private IObject featureConfigurationMock1;
    private IObject featureConfigurationMock2;

    private IKey pluginLoaderKey = mock(IKey.class);
    private IPluginLoader pluginLoaderMock;

    private IKey queueKey = mock(IKey.class);
    private IQueue queueMock = mock(IQueue.class);

    private ArgumentCaptor<Object> argsCaptor = ArgumentCaptor.forClass(Object.class);

    private ITask taskMock = mock(ITask.class);

    @Before
    public void setUp()
            throws Exception {
        pluginCreatorMock = mock(IPluginCreator.class);
        pluginLoaderVisitorMock = mock(IPluginLoaderVisitor.class);
        configurationManagerMock = mock(IConfigurationManager.class);
        filesystemFacadeMock = mock(IFilesystemFacade.class);
        featureStatusMock1 = mock(FeatureStatusImpl.class);
        featureStatusMock2 = mock(FeatureStatusImpl.class);
        featureConfigurationMock1 = mock(IObject.class);
        featureConfigurationMock2 = mock(IObject.class);
        pluginLoaderMock = mock(IPluginLoader.class);

        mockStatic(IOC.class, Keys.class);
        when(Keys.getOrAdd(eq(IFieldName.class.getCanonicalName()))).thenReturn(fieldNameKey);
        when(Keys.getOrAdd(eq("plugin creator"))).thenReturn(pluginCreatorKey);
        when(Keys.getOrAdd(eq("plugin loader visitor"))).thenReturn(pluginLoaderVisitorKey);
        when(Keys.getOrAdd(eq(IConfigurationManager.class.getCanonicalName()))).thenReturn(configurationManagerKey);
        when(Keys.getOrAdd(eq("filesystem facade"))).thenReturn(filesystemFacadeKey);
        when(Keys.getOrAdd(eq(IObject.class.getCanonicalName()))).thenReturn(iobjectKey);
        when(Keys.getOrAdd(eq(FeatureStatusImpl.class.getCanonicalName()))).thenReturn(featureStatusKey);
        when(Keys.getOrAdd(eq("configuration object"))).thenReturn(configurationObjectKey);
        when(Keys.getOrAdd(eq("plugin loader"))).thenReturn(pluginLoaderKey);
        when(Keys.getOrAdd(eq("feature group load completion task queue"))).thenReturn(queueKey);

        when(IOC.resolve(same(fieldNameKey), eq("featureName"))).thenReturn(featureNameFN);
        when(IOC.resolve(same(fieldNameKey), eq("afterFeatures"))).thenReturn(afterFeaturesFN);
        when(IOC.resolve(same(pluginCreatorKey))).thenReturn(pluginCreatorMock);
        when(IOC.resolve(same(pluginLoaderVisitorKey))).thenReturn(pluginLoaderVisitorMock);
        when(IOC.resolve(same(configurationManagerKey))).thenReturn(configurationManagerMock);
        when(IOC.resolve(same(filesystemFacadeKey))).thenReturn(filesystemFacadeMock);
        when(IOC.resolve(same(queueKey))).thenReturn(queueMock);

        //noinspection unchecked
        when(IOC.resolve(same(iobjectKey))).thenReturn(metafeatureConfigMock).thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(featureStatusKey), any(), any()))
                .thenReturn(featureStatusMock1).thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(configurationObjectKey), any()))
                .thenReturn(featureConfigurationMock1).thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(pluginLoaderKey), any(), any(), any()))
                .thenReturn(pluginLoaderMock).thenThrow(ResolutionException.class);
    }

    @Test
    public void Should_constructorNotThrow()
            throws Exception {
        assertNotNull(new FeatureLoader());
    }

    @Test
    public void Should_getFeatureStatusNotCreateDuplicateStatuses()
            throws Exception {
        FeatureLoader featureLoader = new FeatureLoader();

        IFeatureStatus status1 = featureLoader.getFeatureStatus("some feature");
        IFeatureStatus status2 = featureLoader.getFeatureStatus("some feature");

        assertSame(featureStatusMock1, status1);
        assertSame(featureStatusMock1, status2);
    }

    @Test
    public void Should_loadSingleFeature()
            throws Exception {
        IPlugin pluginMock1 = mock(IPlugin.class);
        IPath directoryPathMock = mock(IPath.class);

        when(queueMock.tryTake()).thenReturn(taskMock).thenReturn(null);

        when(featureConfigurationMock1.getValue(featureNameFN)).thenReturn("the feature");
        when(featureConfigurationMock1.getValue(afterFeaturesFN)).thenReturn(Collections.emptyList());

        FeatureLoader featureLoader = new FeatureLoader();

        IFeatureStatus status = featureLoader.loadFeature(directoryPathMock);

        assertSame(featureStatusMock1, status);

        verify(featureStatusMock1).init(directoryPathMock, featureConfigurationMock1);

        verifyStatic(times(1));
        IOC.resolve(same(featureStatusKey), argsCaptor.capture());

        IBiAction loadAction = (IBiAction) argsCaptor.getAllValues().get(1);

        argsCaptor.getAllValues().clear();

        loadAction.execute(featureConfigurationMock1, directoryPathMock);

        verifyStatic();
        IOC.resolve(same(pluginLoaderKey), argsCaptor.capture());

        IAction classHandler = (IAction) argsCaptor.getAllValues().get(1);

        when(pluginCreatorMock.create(same(getClass()), any())).thenReturn(pluginMock1);
        classHandler.execute(getClass());
        verify(pluginMock1).load();

        verify(taskMock, times(0)).execute();
        ArgumentCaptor<IAction> actionArgumentCaptor = ArgumentCaptor.forClass(IAction.class);
        verify(featureStatusMock1).whenDone(actionArgumentCaptor.capture());
        actionArgumentCaptor.getValue().execute(null);
        verify(taskMock, times(1)).execute();
    }

    @Test
    public void Should_loadFeatureGroup_justReturnStatusIfItIsAlreadyInitialized()
            throws Exception {
        when(featureStatusMock1.isInitialized()).thenReturn(true);
        IPath pathMock = mock(IPath.class);

        FeatureLoader featureLoader = new FeatureLoader();

        IFeatureStatus status = featureLoader.loadGroup(pathMock);

        assertSame(featureStatusMock1, status);
        verify(featureStatusMock1).isInitialized();
        verifyNoMoreInteractions(featureStatusMock1);
    }

    @Test
    public void Should_loadFeatureGroup_loadFeaturesFromDirectory()
            throws Exception {
        IPath groupPathMock = mock(IPath.class);
        IPath featurePathMock = mock(IPath.class);
        IPath featureConfigPathMock = mock(IPath.class);

        when(filesystemFacadeMock.listSubdirectories(same(groupPathMock))).thenReturn(Collections.singletonList(featurePathMock));
        when(filesystemFacadeMock.joinPaths(same(featurePathMock), eq(new Path("config.json")))).thenReturn(featureConfigPathMock);

        when(featureConfigurationMock1.getValue(featureNameFN)).thenReturn("feature1");
        when(featureConfigurationMock1.getValue(afterFeaturesFN)).thenReturn(Collections.emptyList());

        //noinspection unchecked
        when(IOC.resolve(same(iobjectKey)))
                .thenReturn(metafeatureConfigMock)
                .thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(featureStatusKey), any(), any()))
                .thenReturn(featureStatusMock1)
                .thenReturn(featureStatusMock2)
                .thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(configurationObjectKey), any()))
                .thenReturn(featureConfigurationMock1)
                .thenReturn(featureConfigurationMock2)
                .thenThrow(ResolutionException.class);
        //noinspection unchecked
        when(IOC.resolve(same(pluginLoaderKey), any(), any(), any()))
                .thenReturn(pluginLoaderMock)
                .thenReturn(pluginLoaderMock)
                .thenThrow(ResolutionException.class);

        doAnswer(invocationOnMock -> {
            when(featureStatusMock2.isInitialized()).thenReturn(true);
            return null;
        }).when(featureStatusMock2).init(any(), any());

        FeatureLoader featureLoader = new FeatureLoader();

        IFeatureStatus status = featureLoader.loadGroup(groupPathMock);

        assertSame(featureStatusMock1, status);
    }
}
