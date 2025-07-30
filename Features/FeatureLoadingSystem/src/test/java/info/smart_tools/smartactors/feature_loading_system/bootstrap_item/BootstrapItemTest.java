package info.smart_tools.smartactors.feature_loading_system.bootstrap_item;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.exception.RevertProcessExecutionException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for BootstrapItem
 */
public class BootstrapItemTest {

    @Test
    public void checkBootstrapItemCreation()
            throws InvalidArgumentException {
        IBootstrapItem item = new BootstrapItem("name");
        assertNotNull(item);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnItemCreation()
            throws InvalidArgumentException {
        new BootstrapItem(null);
        fail();
    }

    @Test
    public void checkMultiParamCreationAndUsage()
            throws Exception {
        Checker ckr1 = new Checker();
        Checker ckr2 = new Checker();
        String name = "name";
        IBootstrapItem item = new BootstrapItem(name)
                .after("a1")
                .after("a2")
                .before("b1")
                .before("b2")
                .before("b3")
                .process(
                        () -> ckr1.wasCalled = true
                )
                .revertProcess(
                        () -> ckr2.wasCalled = true
                );
        item.executeProcess();
        item.executeRevertProcess();
        assertTrue(ckr1.wasCalled);
        assertTrue(ckr2.wasCalled);
        assertEquals(item.getAfterItems().size(), 2);
        assertEquals(item.getBeforeItems().size(), 3);
        assertEquals(name, item.getItemName());
        assertEquals(item.getAfterItems().get(0), "a1");
        assertEquals(item.getAfterItems().get(1), "a2");
        assertEquals(item.getBeforeItems().get(0), "b1");
        assertEquals(item.getBeforeItems().get(1), "b2");
        assertEquals(item.getBeforeItems().get(2), "b3");
    }

    @Test (expected = ProcessExecutionException.class)
    public void checkProcessExecutionException() throws Exception {
        IBootstrapItem item = new BootstrapItem("name")
                .process(
                        () -> {
                            throw new RuntimeException();
                        }
                );
        item.executeProcess();
        fail();
    }
    @Test (expected = RevertProcessExecutionException.class)
    public void checkRevertProcessExecutionException() throws Exception {
        IBootstrapItem item = new BootstrapItem("name")
                .revertProcess(
                        () -> {
                            throw new RuntimeException();
                        }
                );
        item.executeRevertProcess();
        fail();
    }
}

class Checker {
    public Boolean wasCalled = false;
}
