package info.smart_tools.smartactors.testing.test_report_file_printer;

import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.testing.test_report_file_printer.wrapper.PrintWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestReportFilePrinterTestActor extends PluginsLoadingTestBase {
    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
    }

    @Test
    public void Should_work() throws Exception {
        final Path tempFile = Files.createTempFile("SUPER_TEST_REPORT", ".txt");

        String rootPath = tempFile.getParent().toString();
        String fileName = tempFile.getFileName().toString();

        final TestReportFilePrinterActor printer = new TestReportFilePrinterActor(rootPath);
        PrintWrapper wrapper = mock(PrintWrapper.class);
        IObject report = mock(IObject.class);
        when(report.getValue(new FieldName("body"))).thenReturn("it's a report");
        when(report.getValue(new FieldName("name"))).thenReturn(fileName);
        when(wrapper.getReport()).thenReturn(report);

        printer.print(wrapper);

        Files.exists(Paths.get(rootPath, fileName));
        final byte[] bytes = Files.readAllBytes(Paths.get(rootPath, fileName));
        Assert.assertEquals(new String(bytes), "it's a report");
    }
}
