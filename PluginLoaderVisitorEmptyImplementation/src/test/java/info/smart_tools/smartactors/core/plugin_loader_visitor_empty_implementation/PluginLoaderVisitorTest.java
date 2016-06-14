package info.smart_tools.smartactors.core.plugin_loader_visitor_empty_implementation;

import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import org.junit.Test;

/**
 * Tests for {@link PluginLoaderVisitor}
 */
public class PluginLoaderVisitorTest {

    @Test
    public void checkPluginLoadingFail() {
        IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
        visitor.pluginLoadingFail("", new Exception());
    }

    @Test
    public void checkPluginLoadingSuccess() {
        IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
        visitor.pluginLoadingSuccess("");
    }

    @Test
    public void checkPackageLoadingFail() {
        IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
        visitor.packageLoadingFail("", new Exception());
    }

    @Test
    public void checkPackageLoadingSuccess() {
        IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
        visitor.packageLoadingSuccess("");
    }

}
