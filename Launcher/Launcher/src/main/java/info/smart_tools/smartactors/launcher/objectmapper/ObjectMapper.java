package info.smart_tools.smartactors.launcher.objectmapper;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ObjectMapperInstanceException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ObjectMapper implements IObjectMapper {

    private final Object objectMapper;

    private final Method readValue;

    public ObjectMapper(
            final ClassLoader classLoader
    ) throws ObjectMapperInstanceException {
        try {
            Class<?> clazz = classLoader.loadClass("com.fasterxml.jackson.databind.ObjectMapper");
            Class<?> deserializationFeature = classLoader.loadClass("com.fasterxml.jackson.databind.DeserializationFeature");

            Method configure = clazz.getDeclaredMethod("configure", deserializationFeature, boolean.class);
            Method readValue = clazz.getDeclaredMethod("readValue", String.class, Class.class);

            Object objectMapper = clazz.newInstance();
            Object failOnUnknownProperties = Arrays.stream(deserializationFeature.getEnumConstants())
                    .filter(it -> it.toString().equals("FAIL_ON_UNKNOWN_PROPERTIES"))
                    .findFirst()
                    .orElseThrow(() -> new ObjectMapperInstanceException("Failed to find property \"FAIL_ON_UNKNOWN_PROPERTIES\""));
            configure.invoke(objectMapper, failOnUnknownProperties, false);

            this.objectMapper = objectMapper;
            this.readValue = readValue;
        } catch (ClassNotFoundException e) {
            throw new ObjectMapperInstanceException("Failed to find class in class loader", e);
        } catch (InvocationTargetException e) {
            throw new ObjectMapperInstanceException("Failed to invoke method to configure ObjectMapper", e);
        } catch (NoSuchMethodException e) {
            throw new ObjectMapperInstanceException("Failed to find methods in ObjectMapper", e);
        } catch (InstantiationException e) {
            throw new ObjectMapperInstanceException("Failed to create instance of ObjectMapper", e);
        } catch (IllegalAccessException e) {
            throw new ObjectMapperInstanceException("Failed to access methods of ObjectMapper", e);
        }
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
