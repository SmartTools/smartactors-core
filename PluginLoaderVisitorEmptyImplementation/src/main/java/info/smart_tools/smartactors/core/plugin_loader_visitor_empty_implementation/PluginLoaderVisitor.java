package info.smart_tools.smartactors.core.plugin_loader_visitor_empty_implementation;

import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;

/**
 * Empty implementation of {@link IPluginLoaderVisitor}
 * @param <String> type of inspected object
 */
public class PluginLoaderVisitor<String> implements IPluginLoaderVisitor<String> {

    @Override
    public void pluginLoadingFail(final String str, final Throwable e) {

    }

    @Override
    public void packageLoadingFail(final String str, final Throwable e) {

    }

    @Override
    public void pluginLoadingSuccess(final String str) {

    }

    @Override
    public void packageLoadingSuccess(final String str) {

    }
}
