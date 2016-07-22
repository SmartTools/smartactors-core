package info.smart_tools.smartactors.core.iresponse_content_strategy;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iresponse.IResponse;

/**
 * Interface for strategy, that should set content of the response
 */
public interface IResponseContentStrategy {
    /**
     * Method for setting content of the response, from {@link IObject} response
     * @param responseObject IObject from environment
     * @param response Response in which content should add
     *
     * @throws SerializeException if there is error on serialization
     */
    void setContent(final IObject responseObject, final IResponse response) throws SerializeException;
}
