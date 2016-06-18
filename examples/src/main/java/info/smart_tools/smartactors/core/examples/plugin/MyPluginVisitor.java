package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;

/**
 *  Simplest visitor which just prints the calls to System.out.
 */
public class MyPluginVisitor implements IPluginLoaderVisitor<String> {

    @Override
    public void pluginLoadingFail(String value, Throwable e) {
        System.out.println(value + " plugin load failed");
        System.out.println(String.valueOf(e));
    }

    @Override
    public void packageLoadingFail(String value, Throwable e) {
        System.out.println(value + " package load failed");
        System.out.println(String.valueOf(e));
    }

    @Override
    public void pluginLoadingSuccess(String value) {
        System.out.println(value + " plugin loaded successfully");
    }

    @Override
    public void packageLoadingSuccess(String value) {
        System.out.println(value + " package loaded successfully");
    }
}
