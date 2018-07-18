package info.smart_tools.smartactors.endpoint_components_generic.interrupt_client_callback;

import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class InterruptClientCallbackTest extends TrivialPluginsLoadingTestBase {
    private IMessageProcessor messageProcessorMock;
    private IObject req, res, message;

    @Override
    protected void registerMocks() throws Exception {
        messageProcessorMock = mock(IMessageProcessor.class);

        req = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        res = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        message = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        req.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageProcessor"), messageProcessorMock);
        when(messageProcessorMock.getMessage()).thenReturn(message);
    }

    @Test(expected = ClientCallbackException.class)
    public void Should_throwWhenNoMessageProcessorProvidedForStartMethod() throws Exception {
        new InterruptClientCallback().onStart(mock(IObject.class));
    }

    @Test
    public void Should_pauseMessageProcessorOnStart() throws Exception {
        new InterruptClientCallback().onStart(req);

        verify(messageProcessorMock).pauseProcess();
        verifyNoMoreInteractions(messageProcessorMock);
    }

    @Test
    public void Should_continueProcessWithExceptionOnError() throws Exception {
        Throwable err = new Throwable();

        new InterruptClientCallback().onError(req, err);

        verify(messageProcessorMock).continueProcess(same(err));
        verifyNoMoreInteractions(messageProcessorMock);
    }

    @Test
    public void Should_storeResponseAndContinueProcessWhenResponseReceived() throws Exception {
        new InterruptClientCallback().onSuccess(req, res);

        verify(messageProcessorMock).continueProcess((Throwable) isNull());
        verify(messageProcessorMock, atLeast(1)).getMessage();
        verifyNoMoreInteractions(messageProcessorMock);

        assertSame(res, message.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "response")));
    }
}
