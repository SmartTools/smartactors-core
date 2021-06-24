package info.smart_tools.smartactors.launcher.bootstrap;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.BootstrapWrapperInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.RevertProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrapwrapper.IBootstrapWrapper;

import java.lang.reflect.Method;

/**
 * Implementation of {@link IBootstrapWrapper}
 */
public class BootstrapWrapper implements IBootstrapWrapper {

    private final Class<?> bootstrapCls;
    private final Class<?> bootstrapInterface;
    private final Object bootstrap;

    public BootstrapWrapper(
            final IClassLoaderWrapper classLoader
    ) throws BootstrapWrapperInitException {
        try {
            this.bootstrapCls = classLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap");
            this.bootstrapInterface = classLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap");

            this.bootstrap = bootstrapCls.newInstance();
        } catch (Exception e) {
            throw new BootstrapWrapperInitException("Failed to load bootstrap classes", e);
        }
    }

    @Override
    public void add(Object bootstrapItem) {

    }

    @Override
    public void start() throws ProcessExecutionException {
        try {
            Method bootstrapStart = bootstrapCls.getMethod("start");
            bootstrapStart.invoke(bootstrap);
        } catch (Exception e) {
            throw new ProcessExecutionException("Failed to start bootstrap", e);
        }
    }

    @Override
    public void revert() throws RevertProcessExecutionException {
        try {
            Method bootstrapRevert = bootstrapCls.getMethod("revert");
            bootstrapRevert.invoke(bootstrap);
        } catch (Exception e) {
            throw new RevertProcessExecutionException("Failed to revert bootstrap", e);
        }
    }

    @Override
    public Object getInstance() {
        return bootstrap;
    }
}

