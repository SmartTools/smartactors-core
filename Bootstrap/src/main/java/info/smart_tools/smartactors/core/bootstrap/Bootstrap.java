package info.smart_tools.smartactors.core.bootstrap;

import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IBootstrap}
 */
public class Bootstrap implements IBootstrap<IBootstrapItem<String>> {

    private List<IBootstrapItem<String>> itemStorage = new ArrayList<>();

    @Override
    public void add(final IBootstrapItem<String> bootstrapItem) {
        itemStorage.add(bootstrapItem);
    }

    @Override
    public void start()
            throws ProcessExecutionException {
        try {
            TopologicalSort ts = new TopologicalSort(itemStorage);
            List<IBootstrapItem<String>> orderedItems = ts.getOrderedList(false);
            for (IBootstrapItem<String> item : orderedItems) {
                item.executeProcess();
            }
        } catch (Throwable e) {
            throw new ProcessExecutionException("Could not execute plugin process.", e);
        }
    }

    @Override
    public void revert()
            throws RevertProcessExecutionException {
        try {
            TopologicalSort ts = new TopologicalSort(itemStorage);
            List<IBootstrapItem<String>> orderedItems = ts.getOrderedList(true);
            for (IBootstrapItem<String> item : orderedItems) {
                item.executeRevertProcess();
            }
        } catch (Throwable e) {
            throw new RevertProcessExecutionException("Could not execute plugin revert process.", e);
        }
    }
}

