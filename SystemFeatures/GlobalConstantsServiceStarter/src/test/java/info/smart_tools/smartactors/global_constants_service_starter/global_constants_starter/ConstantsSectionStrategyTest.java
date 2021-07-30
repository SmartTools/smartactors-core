package info.smart_tools.smartactors.global_constants_service_starter.global_constants_starter;

import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test for {@link ConstantsSectionStrategy}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class ConstantsSectionStrategyTest {
    private IKey fieldNameKey = mock(IKey.class);
    private IFieldName constFieldName = mock(IFieldName.class);
    private IFieldName constNameFieldName = mock(IFieldName.class);
    private IFieldName constValueFieldName = mock(IFieldName.class);
    private IFieldName const1FieldName = mock(IFieldName.class);
    private IFieldName const2FieldName = mock(IFieldName.class);
    private IKey globalConstantsObjectKey = mock(IKey.class);
    private IObject globalConstantsObjectMock;
    private IObject const1ObjectMock;
    private IObject const2ObjectMock;
    private IObject configMock;

    @Before
    public void setUp()
            throws Exception {
        mockStatic(IOC.class, Keys.class);

        when(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName")).thenReturn(fieldNameKey);
        when(IOC.resolve(fieldNameKey, "const")).thenReturn(constFieldName);
        when(IOC.resolve(fieldNameKey, "name")).thenReturn(constNameFieldName);
        when(IOC.resolve(fieldNameKey, "value")).thenReturn(constValueFieldName);
        when(IOC.resolve(fieldNameKey, "CONST_1")).thenReturn(const1FieldName);
        when(IOC.resolve(fieldNameKey, "CONST_2")).thenReturn(const2FieldName);

        globalConstantsObjectMock = mock(IObject.class);
        const1ObjectMock = mock(IObject.class);
        const2ObjectMock = mock(IObject.class);

        when(const1ObjectMock.getValue(constNameFieldName)).thenReturn("CONST_1");
        when(const1ObjectMock.getValue(constValueFieldName)).thenReturn(1);

        when(const2ObjectMock.getValue(constNameFieldName)).thenReturn("CONST_2");
        when(const2ObjectMock.getValue(constValueFieldName)).thenReturn("2");

        configMock = mock(IObject.class);

        when(configMock.getValue(constFieldName)).thenReturn(Arrays.asList(const1ObjectMock, const2ObjectMock));

        when(Keys.getKeyByName("global constants")).thenReturn(globalConstantsObjectKey);
        when(IOC.resolve(globalConstantsObjectKey)).thenReturn(globalConstantsObjectMock);
    }

    @Test
    public void Should_writeConstantsToGlobalObject()
            throws Exception {
        ISectionStrategy strategy = new ConstantsSectionStrategy();

        strategy.onLoadConfig(configMock);

        verify(globalConstantsObjectMock).setValue(const1FieldName, 1);
        verify(globalConstantsObjectMock).setValue(const2FieldName, "2");
    }

    @Test
    public void Should_resolveSectionFieldName()
            throws Exception {
        ISectionStrategy strategy = new ConstantsSectionStrategy();

        assertSame(constFieldName, strategy.getSectionName());
    }

    @Test
    public void Should_wrapExceptionOccurredProcessingConfig()
            throws Exception {
        ISectionStrategy strategy = new ConstantsSectionStrategy();

        doThrow(ChangeValueException.class).when(globalConstantsObjectMock).setValue(any(), any());
        doThrow(ResolutionException.class).when(globalConstantsObjectMock).deleteField(any());

        try {
            strategy.onLoadConfig(configMock);
            fail();
        } catch (ConfigurationProcessingException e) { }

        try {
            strategy.onRevertConfig(configMock);
            fail();
        } catch (ConfigurationProcessingException e) {
            assertEquals(2, e.getSuppressed().length);
        }

        doThrow(ResolutionException.class).when(configMock).getValue(any());

        try {
            strategy.onRevertConfig(configMock);
            fail();
        } catch (ConfigurationProcessingException e) {
            assertEquals(1, e.getSuppressed().length);
        }
    }

    @Test
    public void Should_deleteConstantsToGlobalObject()
            throws Exception {
        ISectionStrategy strategy = new ConstantsSectionStrategy();

        strategy.onRevertConfig(configMock);

        verify(globalConstantsObjectMock).deleteField(const1FieldName);
        verify(globalConstantsObjectMock).deleteField(const2FieldName);
    }

}
