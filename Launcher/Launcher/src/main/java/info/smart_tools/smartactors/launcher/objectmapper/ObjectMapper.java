package info.smart_tools.smartactors.launcher.objectmapper;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.ReadJsonException;
import info.smart_tools.smartactors.launcher.model.FeatureConfig;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ObjectMapper implements IObjectMapper {

    private final Object objectMapper;

    private final Method readValue;

    private ObjectMapper(
            final Object objectMapper,
            final Method readValue
    ) {
        this.objectMapper = objectMapper;
        this.readValue = readValue;
    }

    public static ObjectMapper newInstance(
            final ClassLoader classLoader
    ) throws Exception {
        Class<?> clazz = classLoader.loadClass("com.fasterxml.jackson.databind.ObjectMapper");
        Class<?> deserializationFeature = classLoader.loadClass("com.fasterxml.jackson.databind.DeserializationFeature");

        Method configure = clazz.getDeclaredMethod("configure", deserializationFeature, boolean.class);
        Method readValue = clazz.getDeclaredMethod("readValue", String.class, Class.class);

        Object objectMapper = clazz.newInstance();
        // TODO: properly handle possible missing enum value
        Object failOnUnknownProperties = Arrays.stream(deserializationFeature.getEnumConstants())
                .filter(it -> it.toString().equals("FAIL_ON_UNKNOWN_PROPERTIES"))
                .findFirst()
                .get();
        configure.invoke(objectMapper, failOnUnknownProperties, false);
        return new ObjectMapper(objectMapper, readValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readJson(
            final String json,
            final Class<T> classType
    ) throws ReadJsonException {
        try {
            return (T) readValue.invoke(objectMapper, json, classType);
        } catch (Exception e) {
            throw new ReadJsonException(e);
        }
    }
}
