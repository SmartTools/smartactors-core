package info.smart_tools.smartactors.core.ienvironment_extractor;

import info.smart_tools.smartactors.core.ienvironment_extractor.exceptions.EnvironmentExtractionException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Interface for environment extractor from request
 */
public interface IEnvironmentExtractor {
    /**
     * Method for extract environment from request
     *
     * @param request request
     * @param ctx     context of the request
     * @return environment {@link IObject} that should contain message and context
     *
     * @throws EnvironmentExtractionException 
     */
    IObject extract(final Object request, final Object ctx) throws EnvironmentExtractionException;
}
