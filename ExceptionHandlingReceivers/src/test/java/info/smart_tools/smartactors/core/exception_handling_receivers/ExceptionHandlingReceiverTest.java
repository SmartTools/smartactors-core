package info.smart_tools.smartactors.core.exception_handling_receivers;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Base class for tests on classes extending {@link ExceptionHandlingReceiver}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class})
@Ignore
public class ExceptionHandlingReceiverTest {
    protected IFieldName causeLevelFieldName = mock(IFieldName.class);
    protected IFieldName causeStepFieldName = mock(IFieldName.class);
    protected IFieldName catchLevelFieldName = mock(IFieldName.class);
    protected IFieldName catchStepFieldName = mock(IFieldName.class);
    protected IFieldName exceptionFieldName = mock(IFieldName.class);

    protected IKey keyForKeyStore = mock(IKey.class);
    protected IKey keyForFieldName = mock(IKey.class);

    protected IMessageProcessor messageProcessorMock;
    protected IObject contextMock;

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(keyForKeyStore);
        when(IOC.resolve(same(keyForKeyStore), eq(IFieldName.class.toString()))).thenReturn(keyForFieldName);

        when(IOC.resolve(same(keyForFieldName), eq("causeLevel"))).thenReturn(causeLevelFieldName);
        when(IOC.resolve(same(keyForFieldName), eq("causeStep"))).thenReturn(causeStepFieldName);
        when(IOC.resolve(same(keyForFieldName), eq("catchLevel"))).thenReturn(catchLevelFieldName);
        when(IOC.resolve(same(keyForFieldName), eq("catchStep"))).thenReturn(catchStepFieldName);
        when(IOC.resolve(same(keyForFieldName), eq("exception"))).thenReturn(exceptionFieldName);

        messageProcessorMock = mock(IMessageProcessor.class);
        contextMock = mock(IObject.class);
        when(messageProcessorMock.getContext()).thenReturn(contextMock);
    }

}
