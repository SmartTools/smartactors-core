package info.smart_tools.smartactors.core.receiver_generator;

import info.smart_tools.smartactors.core.receiver_generator.CustomActor;
import info.smart_tools.smartactors.core.receiver_generator.ICustomWrapper;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;

public class CustomActor_doSomeWork_receiver implements IMessageReceiver {
    private CustomActor usersObject;
    private IResolveDependencyStrategy strategy;

    public CustomActor_doSomeWork_receiver(CustomActor object, IResolveDependencyStrategy strategy)  {
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

}