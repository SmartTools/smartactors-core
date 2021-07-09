package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;

public interface IObjectMapper {

    <T> T readJson(String json, Class<T> classType) throws ReadJsonException;
}
