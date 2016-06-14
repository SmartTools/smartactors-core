package info.smart_tools.smartactors.core.bootstrap;

import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sort given list of {@link IBootstrapItem} using topological sorting graph.
 * HardCode.
 * Using Depth-first search (DFS) algorithm.
 */
class TopologicalSort {
    /** graph node state: 0 - not visited, 1 - visited, 2 - stored */
    private int[] states;
    private int size;
    private List<Integer>[] edges;
    private List<IBootstrapItem<String>> items;
    private List<IBootstrapItem<String>> sortedItems;
    private Map<Integer, IBootstrapItem<String>> numberToItemMap;
    private Map<IBootstrapItem<String>, Integer> itemToNumberMap;
    private Map<String, IBootstrapItem<String>> nameToItemMap;

    /**
     * Creates class that forms orderly related list of {@link IBootstrapItem}.
     * First element hasn't dependencies, each following may has dependency only with previous.
     * @param items map of depended instances of {@link IBootstrapItem}
     * @throws Exception throws if graph has cycle or any errors occurred
     */
    TopologicalSort(final List<IBootstrapItem<String>> items)
            throws Exception {
        this.items = items;
        this.size = items.size();
        this.edges = new ArrayList[this.size];
        this.sortedItems = new ArrayList<>();
        this.states = new int[this.size];
        this.numberToItemMap = new HashMap<>();
        this.itemToNumberMap = new HashMap<>();
        this.nameToItemMap = new HashMap<>();
        fillSupportingHashMaps();
        fillEdges();
        if (!topologicalSort()) {
            throw new Exception("Graph has cycle.");
        }
    }

    private void fillSupportingHashMaps() {
        for (int i = 0; i < items.size(); ++i) {
            numberToItemMap.put(i, items.get(i));
            itemToNumberMap.put(items.get(i), i);
            nameToItemMap.put(items.get(i).getItemName(), items.get(i));
        }
    }

    /**
     * Fill edges list and change 'before' dependency by 'after'.
     */
    private void fillEdges()
            throws Exception {

        // fill lists of edges
        for (int i = 0; i < items.size(); ++i) {
            IBootstrapItem<String> item = items.get(i);
            String name = item.getItemName();

            // fill 'after' dependencies
            List<String> dependencies = item.getAfterItems();
            List<Integer> numberedDependencies = new ArrayList<>();
            for (String dependency : dependencies) {
                IBootstrapItem<String> afterItem = nameToItemMap.get(dependency);
                if (null == afterItem) {
                    throw new Exception("Reference to a non-existing dependency.");
                }
                numberedDependencies.add(itemToNumberMap.get(afterItem));
            }
            edges[i] = numberedDependencies;

            // loop for find dependencies from other items
            for (IBootstrapItem<String> innerLoopItem : items) {
                for (String before : innerLoopItem.getBeforeItems()) {
                    if (before.equals(name)) {
                        edges[i].add(itemToNumberMap.get(innerLoopItem));
                    }
                }
            }
        }
    }

    private boolean topologicalSort()
            throws Exception {
        boolean cycle;
        for (int i = 0; i <= this.size - 1; ++i) {
            cycle = dfs(i);
            if (cycle) {
                return false;
            }
        }

        return true;
    }

    private boolean dfs(final int v) {
        if (1 == states[v]) {
            return true;
        }
        if (2 == states[v]) {
            return false;
        }
        states[v] = 1;
        for (int i = 0; i < this.edges[v].size(); ++i) {
            if (dfs(this.edges[v].get(i))) {
                return true;
            }
        }
        sortedItems.add(items.get(v));
        states[v] = 2;

        return false;
    }

    /**
     * Return ordered list of {@link IBootstrapItem}
     * @param reverted revert obtained list
     * @return list of {@link IBootstrapItem}
     */
    public List<IBootstrapItem<String>> getOrderedList(final boolean reverted) {
        if (reverted) {
            List<IBootstrapItem<String>> revertedSortedItemsList = this.sortedItems.subList(0, this.sortedItems.size());
            Collections.reverse(revertedSortedItemsList);
            return revertedSortedItemsList;
        }
        return this.sortedItems;
    }
}
