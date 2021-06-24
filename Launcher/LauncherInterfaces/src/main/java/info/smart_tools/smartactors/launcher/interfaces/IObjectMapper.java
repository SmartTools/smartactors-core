package info.smart_tools.smartactors.launcher.interfaces;

import info.smart_tools.smartactors.launcher.interfaces.exception.ReadJsonException;
import info.smart_tools.smartactors.launcher.model.FeatureConfig;

public interface IObjectMapper {

    <T> T readJson(String json, Class<T> classType) throws ReadJsonException;
}
