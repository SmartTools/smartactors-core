package info.smart_tools.smartactors.feature_loading_system.bootstrap;


import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IBootstrap}
 */
public class Bootstrap implements IBootstrap<IBootstrapItem<String>> {

    private List<IBootstrapItem<String>> itemStorage = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Bootstrap() {
    }

    /**
     * Constructor.
     * Creates instance of {@link IBootstrap} and initialize field {@code loadedItems} by given agrument
     * @param loadedItems the list of already loaded items
     */
    public Bootstrap(final Collection<IBootstrapItem<String>> loadedItems) {
        for (IBootstrapItem<String> item : loadedItems) {
            this.itemStorage.add(item.process(() -> { }));
        }
    }

    @Override
    public void add(final IBootstrapItem<String> bootstrapItem) {
        itemStorage.add(bootstrapItem);
    }

    @Override
    public List<IBootstrapItem<String>> start()
            throws ProcessExecutionException {
        try {
            TopologicalSort ts = new TopologicalSort(itemStorage);
            List<IBootstrapItem<String>> orderedItems = ts.getOrderedList(false);
            List<IBootstrapItem<String>> doneItems = new ArrayList<>(orderedItems.size());
            for (IBootstrapItem<String> item : orderedItems) {
                try {
                    item.executeProcess();
                    doneItems.add(item);
                } catch (Throwable e) {
                    throw new ProcessExecutionException(
                            MessageFormat.format(
                                    "\n\nError occurred processing item \"{0}\".\nProcessed items are: {1}.\nAll items are: {2}.\nCause: {3}.",
                                    item.getItemName(),
                                    String.join(", ", doneItems.stream().map(IBootstrapItem::getItemName).collect(Collectors.toList())),
                                    String.join(", ", orderedItems.stream().map(IBootstrapItem::getItemName).collect(Collectors.toList())),
                                    e.getMessage()),
                            e);
                }
            }
            return doneItems;
        } catch (ProcessExecutionException e) {
            throw e;
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
