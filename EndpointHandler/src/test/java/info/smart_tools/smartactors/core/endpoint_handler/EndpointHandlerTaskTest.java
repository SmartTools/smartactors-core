package info.smart_tools.smartactors.core.endpoint_handler;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.ienvironment_extractor.IEnvironmentExtractor;
import info.smart_tools.smartactors.core.ienvironment_extractor.exceptions.EnvironmentExtractionException;
import info.smart_tools.smartactors.core.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import org.junit.Test;
import org.omg.CORBA.Object;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndpointHandlerTaskTest {

    private IEnvironmentExtractor environmentExtractor;
    private Object context;
    private Object request;
    private IEnvironmentHandler environmentHandler;
    private IReceiverChain receiverChain;

    @Test
    public void executeTest() throws TaskExecutionException, EnvironmentExtractionException, InvalidArgumentException {
        environmentExtractor = mock(IEnvironmentExtractor.class);
        context = mock(Object.class);
        request = mock(Object.class);
        environmentHandler = mock(IEnvironmentHandler.class);
        receiverChain = mock(IReceiverChain.class);

        EndpointHandlerTask task = new EndpointHandlerTask(environmentExtractor, context,
                request, environmentHandler, receiverChain);
        IObject environment = new DSObject("{\"hello\": \"world\"}");
        when(environmentExtractor.extract(request, context)).thenReturn(environment);
        task.execute();
        verify(environmentExtractor).extract(request, context);
        verify(environmentHandler).handle(environment, receiverChain);
    }
}
