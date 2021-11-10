package info.smart_tools.smartactors.ioc_viewer_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;

public class IocViewerPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public IocViewerPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> iocViewerItem = new BootstrapItem("ioc-viewer-plugin");

            iocViewerItem
                    .process(() -> {
                        try {
                            Thread debugThread = new IocViewerThread();
                            debugThread.start();
                        } catch (Exception e) {
                            throw new ActionExecutionException("Failed to start ioc viewer thread", e);
                        }
                    });

            bootstrap.add(iocViewerItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Failed to initialize bootstrap item", e);
        }
    }
}
