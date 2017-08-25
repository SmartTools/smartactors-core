package info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

import java.util.Collection;

/**
 * Created by sevenbits on 07.02.17.
 */
public interface ScatterWrapper {
    Collection<Object> getCollection() throws ReadValueException;

    IMessageProcessor getMessageProcessor() throws ReadValueException;
}
