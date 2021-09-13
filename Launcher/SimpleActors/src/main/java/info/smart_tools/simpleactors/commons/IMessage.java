package info.smart_tools.simpleactors.commons;

public interface IMessage {

    <T> T get(final String key);

    void put(final String key, final Object value);
}
