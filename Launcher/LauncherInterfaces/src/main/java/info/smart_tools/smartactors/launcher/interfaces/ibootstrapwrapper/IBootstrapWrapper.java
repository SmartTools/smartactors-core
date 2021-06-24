package info.smart_tools.smartactors.launcher.interfaces.ibootstrapwrapper;

import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.RevertProcessExecutionException;

public interface IBootstrapWrapper {

    void add(Object bootstrapItem);

    void start() throws ProcessExecutionException;

    void revert() throws RevertProcessExecutionException;

    Object getInstance();
}
