package info.smart_tools.smartactors.launcher.bootstrap;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.BootstrapInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.ProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.RevertProcessExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;

import java.lang.reflect.Method;

public class Bootstrap implements IBootstrap {

    private final Class<?> bootstrapCls;
    private final Object bootstrap;

    public Bootstrap(
            final IClassLoaderWrapper classLoader
    ) throws BootstrapInitException {
        try {
            this.bootstrapCls = classLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap");

            this.bootstrap = bootstrapCls.newInstance();
        } catch (Exception e) {
            throw new BootstrapInitException("Failed to load bootstrap classes", e);
        }
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

