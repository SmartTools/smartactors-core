package info.smart_tools.smartactors.launcher.interfaces.ibootstrap;

import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.RevertProcessExecutionException;

public interface IBootstrap {

    void add(Object bootstrapItem);

    void start() throws ProcessExecutionException;

    void revert() throws RevertProcessExecutionException;

    Object getInstance();
}
