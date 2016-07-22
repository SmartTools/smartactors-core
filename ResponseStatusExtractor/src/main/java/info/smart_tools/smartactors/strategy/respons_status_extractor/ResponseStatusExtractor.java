package info.smart_tools.smartactors.strategy.respons_status_extractor;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iresponse_status_extractor.IResponseStatusExtractor;

/**
 * Simple implementation of {@link IResponseStatusExtractor}, that always return 200
 */
public class ResponseStatusExtractor implements IResponseStatusExtractor {

    @Override
    public Integer extract(final IObject environment) {
        return 200;
    }
}
