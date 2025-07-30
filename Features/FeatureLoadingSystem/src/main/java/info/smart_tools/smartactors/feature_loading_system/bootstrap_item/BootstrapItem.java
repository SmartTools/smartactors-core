package info.smart_tools.smartactors.feature_loading_system.bootstrap_item;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.exception.RevertProcessExecutionException;

import java.util.List;

/**
 * Implementation of {@link IBootstrapItem}
 */
public class BootstrapItem implements IBootstrapItem<String> {

    private ItemCore item;

    /**
     * Constructor with item name as argument
     * @param name name of {@link IBootstrapItem} instance
     * @throws InvalidArgumentException if any errors occurred
     */
    public BootstrapItem(final String name) throws InvalidArgumentException {
        item = new ItemCore(name);
    }

    @Override
    public BootstrapItem before(final String itemName) {
        item.addBefore(itemName);
        return this;
    }

    @Override
    public BootstrapItem after(final String itemName) {
        item.addAfter(itemName);
        return this;
    }

    @Override
    public BootstrapItem process(final IActionNoArgs action) {
        item.setProcess(action);
        return this;
    }

    @Override
    public BootstrapItem revertProcess(final IActionNoArgs action) {
        item.setRevertProcess(action);
        return this;
    }

    @Override
    public void executeProcess()
            throws ProcessExecutionException {
        item.executeProcess();
    }

    @Override
    public void executeRevertProcess()
            throws RevertProcessExecutionException {
        item.executeRevertProcess();
    }

    @Override
    public List<String> getBeforeItems() {
        return this.item.getBeforeList();
    }

    @Override
    public List<String> getAfterItems() {
        return this.item.getAfterList();
    }

    @Override
    public String getItemName() {
        return item.getItemName();
    }
}
