package info.smart_tools.smartactors.endpoint.response;


import info.smart_tools.smartactors.endpoint.interfaces.iresponse.IResponse;

/**
 * Implementation of IResponse to save environment
 */
public class Response implements IResponse {
    private byte[] content;

    @Override
    public void setContent(final byte[] response) {
        content = response;
    }

    @Override
    public byte[] getContent() {
        return content;
    }
}
