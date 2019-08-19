package info.smart_tools.smartactors.feature_loading_system.bootstrap;


import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IBootstrap}
 */
public class Bootstrap implements IBootstrap<IBootstrapItem<String>> {

    private List<IBootstrapItem<String>> itemStorage = new ArrayList<>();
    private List<IBootstrapItem<String>> doneItems = new ArrayList<>();
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
        if (doneItems.size() > 0) {
            throw new ProcessExecutionException("Can't start bootstrap again if some of bootstrap items already started.");
        }
        try {
            TopologicalSort ts = new TopologicalSort(itemStorage);
            List<IBootstrapItem<String>> orderedItems = ts.getOrderedList(false);
            List<IBootstrapItem<String>> failedItems = new ArrayList<>(0);
            for (IBootstrapItem<String> item : orderedItems) {
                try {
                    item.executeProcess();
                    doneItems.add(item);
                    System.out.println("[OK] Initial load of plugin \"" + item.getItemName() + "\" done.");
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    System.out.println("[WARNING] Initial load of plugin \"" + item.getItemName() + "\" failed.");
                    item.executeRevertProcess();
                    failedItems.add(item);
                }
            }

            List<IBootstrapItem<String>> retryDoneItems = new ArrayList<>(0);
            do {
                for (IBootstrapItem<String> item : retryDoneItems) {
                    failedItems.remove(item);
                }
                retryDoneItems.clear();
                for (IBootstrapItem<String> item : failedItems) {
                    try {
                        item.executeProcess();
                        doneItems.add(item);
                        retryDoneItems.add(item);
                        System.out.println("[OK] " + Thread.currentThread().getName() + " Load retry of plugin \"" + item.getItemName() + "\" done.");
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        System.out.println("[WARNING] " + Thread.currentThread().getName() + " Load retry of plugin \"" + item.getItemName() + "\" failed.");
                        item.executeRevertProcess();
                    }
                }
            } while (retryDoneItems.size() > 0);
            if (failedItems.size() > 0) {
                throw new ProcessExecutionException(
                        MessageFormat.format(
                                "\n\nError occurred during feature plugins startup.\nProcessed plugins are: {0}.\nFailed plugins are: {1}.\nAll plugins are: {2}\n",
                                String.join(", ", doneItems.stream().map(IBootstrapItem::getItemName).collect(Collectors.toList())),
                                String.join(", ", failedItems.stream().map(IBootstrapItem::getItemName).collect(Collectors.toList())),
                                String.join(", ", orderedItems.stream().map(IBootstrapItem::getItemName).collect(Collectors.toList()))));
            }
            return doneItems;
        } catch (ProcessExecutionException e) {
            throw e;
        } catch (Throwable e) {
            throw new ProcessExecutionException("Could not start feature plugins.", e);
        }
    }

    @Override
    public void revert()
            throws RevertProcessExecutionException {
        RevertProcessExecutionException exception =
                new RevertProcessExecutionException("Could not execute plugin revert process.");

        if (doneItems.size() == 0) {
            throw new RevertProcessExecutionException("Can't revert bootstrap if no bootstrap items started.");
        }
        ListIterator<IBootstrapItem<String>> i = doneItems.listIterator(doneItems.size());
        while (i.hasPrevious()) {
            IBootstrapItem<String> item = i.previous();
            try {
                item.executeRevertProcess();
                i.remove();
                System.out.println("[OK] " + Thread.currentThread().getName() + " Revert of plugin \"" + item.getItemName() + "\" done.");
            } catch (Throwable e) {
                exception.addSuppressed(e);
                System.out.println("[WARNING] " + Thread.currentThread().getName() + " Revert of plugin \"" + item.getItemName() + "\" failed.");
            }
        }
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }
}
