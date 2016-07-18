package info.smart_tools.smartactors.core.iresponse;


public interface IResponse {

    void setEnvironment(String key, Object environment);

    void setContent(byte[] response);

    byte[] getBody();

    <T> T getEnvironment(String key);
}
