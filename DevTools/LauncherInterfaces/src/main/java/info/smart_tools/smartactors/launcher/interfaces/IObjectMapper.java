package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;

/**
 * Abstract object mapper
 *
 * Reads file and converts it into POJO
 */
public interface IObjectMapper {

    /**
     * Read JSON and convert it into POJO of type {@code T}
     *
     * @param json raw JSON
     * @param classType class to which JSON should be mapped
     * @param <T> type of class passed to {@code classType}
     * @return POJO of type {@code T}
     * @throws ReadJsonException if failed to read JSON as POJO
     */
    <T> T readJson(String json, Class<T> classType) throws ReadJsonException;
}
