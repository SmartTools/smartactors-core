package info.smart_tools.smartactors.downloader.commons;

import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage {
    private final Map<String, Object> storage = new HashMap<>();

    private Message() {
    }

    public static Message.Builder builder() {
        return new Message().new Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        Object value = this.storage.get(key);
            return (T) value;
    }

    public void put(final String key, final Object value) {

        this.storage.put(key, value);
    }

    public final class Builder {
        private Builder() {
        }

        public Message.Builder add(final String key, final Object value) {
            Message.this.put(key, value);
            return this;
        }

        public Message build() {
            return Message.this;
        }
    }
}
