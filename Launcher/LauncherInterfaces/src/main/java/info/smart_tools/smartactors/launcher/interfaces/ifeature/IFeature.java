package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import java.util.List;

public interface IFeature {

    Object getId();
    String getFileName();
    String getName();
    List<String> getAfterFeatures();
}
