package com.my_project.http_client_server_demo.demo_actor;

import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Collections;

public class DemoActor {
    private final IFieldName messageFN;
    private final IFieldName urlFN;
    private final IFieldName messageProcessorFN;
    private final IFieldName contextFN;
    private final IFieldName headersFN;

    public DemoActor() throws ResolutionException {
        urlFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "url");
        messageFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        messageProcessorFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageProcessor");
        contextFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
        headersFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "headers");
    }


    public interface PrepareRequestWrapper {
        IObject getRequestContent() throws ReadValueException;

        String getURL() throws ReadValueException;

        Object getMessageProcessor() throws ReadValueException;

        void setRequest(IObject request) throws ChangeValueException;
    }

    public void prepareRequest(final PrepareRequestWrapper message) throws Exception {
        IObject request = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        request.setValue(messageFN, message.getRequestContent());
        request.setValue(urlFN, message.getURL());
        request.setValue(messageProcessorFN, message.getMessageProcessor());
        request.setValue(contextFN, context);

        context.setValue(headersFN, Collections.emptyList());

        message.setRequest(request);
    }

    public interface ProcessRequestWrapper {
        IObject getReceivedRequest() throws ReadValueException;

        void setResponse(IObject response) throws ChangeValueException;
    }

    public void processRequest(final ProcessRequestWrapper message) throws Exception {
        message.setResponse(message.getReceivedRequest());
    }

    public interface ProcessResponseWrapper {
        IObject getReceivedResponse() throws ReadValueException;

        void setResponse(IObject response) throws ChangeValueException;
    }

    public void processResponse(final ProcessResponseWrapper message) throws Exception {
        message.setResponse(message.getReceivedResponse());
    }
}
