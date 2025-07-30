package info.smart_tools.smartactors.message_processing.receiver_generator;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

public class CustomActor_doSomeWork_receiver implements IMessageReceiver {
    private CustomActor usersObject;
    private IStrategy strategy;

    public CustomActor_doSomeWork_receiver(CustomActor object, IStrategy strategy)  {
        this.usersObject = object;
        this.strategy = strategy;

    }

    public void receive(IMessageProcessor processor) throws MessageReceiveException, AsynchronousOperationException  {
        try {
            ICustomWrapper wrapper = this.strategy.resolve();
            ((IObjectWrapper) wrapper).init(processor.getEnvironment());
            this.usersObject.doSomeWork(wrapper);
        } catch (Throwable e) {
            throw new MessageReceiveException("Could not execute receiver operation.", e);
        }
    }

    @Override
    public void dispose() {
    }
}