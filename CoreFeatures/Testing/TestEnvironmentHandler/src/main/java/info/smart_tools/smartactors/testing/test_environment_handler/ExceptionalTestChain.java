package info.smart_tools.smartactors.testing.test_environment_handler;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of {@link IReceiverChain} used as a exceptional chain for exceptions processing in testing chain.
 */
class ExceptionalTestChain implements IReceiverChain {

    @Override
    public IMessageReceiver get(final int index) {
        return null;
    }

    @Override
    public IObject getArguments(final int index) {
        return null;
    }

    @Override
    public Object getId() {
        return "exceptional test chain";
    }

    @Override
    public Object getName() {
        return "exceptional test chain";
    }

    @Override
    public IScope getScope() {
        return null;
    }

    @Override
    public IModule getModule() {
        return null;
    }

    @Override
    public IObject getExceptionalChainNamesAndEnvironments(final Throwable exception) {
        return null;
    }

    @Override
    public IObject getChainDescription() {
        return null;
    }

    @Override
    public Collection<Object> getExceptionalChainNames() {
        return Collections.emptyList();
    }
}
