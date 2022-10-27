package info.smart_tools.smartactors.http_endpoint.respons_status_extractor;

import info.smart_tools.smartactors.http_endpoint.interfaces.iresponse_status_extractor.IResponseStatusExtractor;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;

/**
 * Simple implementation of {@link IResponseStatusExtractor}.
 */
public class ResponseStatusExtractor implements IResponseStatusExtractor {

    private static final int DEFAULT_STATUS_CODE = 200;

    private IFieldName httpResponseStatusCodeFieldName;
    private IFieldName contextFieldName;

    /**
     * Constructor
     *
     * @throws ResolutionException throws if there are problems with resolving object
     */
    public ResponseStatusExtractor() throws ResolutionException {
        this.httpResponseStatusCodeFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "httpResponseStatusCode"
        );
        this.contextFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "context"
        );
    }

    @Override
    public Integer extract(final IObject environment) {
        Integer status = null;
        try {
            IObject context = (IObject) environment.getValue(this.contextFieldName);
            status = (Integer) context.getValue(this.httpResponseStatusCodeFieldName);
        } catch (Throwable e) {
            // do nothing
        }
        if (null == status) {
            status = DEFAULT_STATUS_CODE;
        }

        return status;
    }
}
