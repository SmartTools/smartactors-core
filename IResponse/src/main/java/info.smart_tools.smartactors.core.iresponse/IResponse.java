package info.smart_tools.smartactors.core.iresponse;

/**
 * Interface for response object
 */
public interface IResponse {
    /**
     * Method, that setting content for response
     * @param response Byte array of response
     */
    void setContent(final byte[] response);

    /**
     * @return content of the response
     */
    byte[] getContent();
}
