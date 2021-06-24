package info.smart_tools.smartactors.launcher.sort;


import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sort given list of {@link D} using topological sorting graph.
 * HardCode.
 * Using Depth-first search (DFS) algorithm.
 */
public class TopologicalSort<D extends IFeature> {
    /** graph node state: 0 - not visited, 1 - visited, 2 - stored */
    private final int[] states;
    private final int size;
    private final List<Integer>[] edges;
    private final List<D> items;
    private final List<D> sortedItems;
    private final Map<Integer, D> numberToItemMap;
    private final Map< D, Integer> itemToNumberMap;
    private final Map<String, D> nameToItemMap;

    /**
     * Creates class that forms orderly related list of {@link D}.
     * First element hasn't dependencies, each following may has dependency only with previous.
     * @param items map of depended instances of {@link D}
     * @throws Exception throws if graph has cycle or any errors occurred
     */
    public TopologicalSort(final List<D> items)
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
            nameToItemMap.put(items.get(i).getName(), items.get(i));
        }
    }

    /**
     * Fill edges list and change 'before' dependency by 'after'.
     */
    private void fillEdges()
            throws Exception {

        // fill lists of edges
        for (int i = 0; i < items.size(); ++i) {
            D item = items.get(i);

            // fill 'after' dependencies
            List<String> dependencies = item.getAfterFeatures();
            List<Integer> numberedDependencies = new ArrayList<>();
            for (String dependency : dependencies) {
                D afterItem = nameToItemMap.get(dependency);
                if (null == afterItem) {
                    throw new Exception(MessageFormat.format("Reference to non-exist dependency \"{0}\" from item \"{1}\".",
                            dependency, item.getName()));
                }
                numberedDependencies.add(itemToNumberMap.get(afterItem));
            }
            edges[i] = numberedDependencies;
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
     * Return ordered list of {@link D}
     * @param reverted revert obtained list
     * @return list of {@link D}
     */
    public List<D> getOrderedList(final boolean reverted) {
        if (reverted) {
            List<D> revertedSortedItemsList = this.sortedItems.subList(0, this.sortedItems.size());
            Collections.reverse(revertedSortedItemsList);
            return revertedSortedItemsList;
        }
        return this.sortedItems;
    }
}

