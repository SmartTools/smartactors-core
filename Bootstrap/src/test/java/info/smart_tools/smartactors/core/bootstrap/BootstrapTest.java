package info.smart_tools.smartactors.core.bootstrap;

import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for Bootstrap
 */
public class BootstrapTest {

    @Test
    public void checkAdditionExecutionAndRevertingBootstrapItem()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = new Bootstrap();
        assertNotNull(bootstrap);
        IBootstrapItem item = mock(IBootstrapItem.class);
        doNothing().when(item).executeProcess();
        doNothing().when(item).executeRevertProcess();
        bootstrap.add(item);
        bootstrap.start();
        verify(item, times(1)).executeProcess();
        bootstrap.revert();
        verify(item, times(1)).executeRevertProcess();
    }

    @Test (expected = ProcessExecutionException.class)
    public void checkProcessExecutionException()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = new Bootstrap();
        IBootstrapItem item = mock(IBootstrapItem.class);
        bootstrap.add(item);
        doThrow(ProcessExecutionException.class).when(item).executeProcess();
        bootstrap.start();
        fail();
    }

    @Test (expected = RevertProcessExecutionException.class)
    public void checkRevertProcessExecutionException()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = new Bootstrap();
        IBootstrapItem item = mock(IBootstrapItem.class);
        bootstrap.add(item);
        doThrow(RevertProcessExecutionException.class).when(item).executeRevertProcess();
        bootstrap.revert();
        fail();
    }
}
