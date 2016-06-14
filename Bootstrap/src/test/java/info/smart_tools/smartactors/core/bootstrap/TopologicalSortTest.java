package info.smart_tools.smartactors.core.bootstrap;

import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Tests for TopologicalSort
 */
public class TopologicalSortTest {

    @Test
    public void checkSortingTwoSimpleGraphs()
            throws Exception {
        IBootstrapItem<String> item1 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item2 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item3 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item4 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item5 = mock(IBootstrapItem.class);

        when(item1.getItemName()).thenReturn("item1");
        when(item2.getItemName()).thenReturn("item2");
        when(item3.getItemName()).thenReturn("item3");
        when(item4.getItemName()).thenReturn("item4");
        when(item5.getItemName()).thenReturn("item5");

        when(item1.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");}});
        when(item3.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item2");}});
        when(item4.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item5.getAfterItems()).thenReturn(new ArrayList<String>(){{}});

        when(item1.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item3.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item4.getBeforeItems()).thenReturn(new ArrayList<String>(){{add("item5");}});
        when(item5.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});

        List<IBootstrapItem<String>> list = new ArrayList<IBootstrapItem<String>>() {
            {
                add(item5);
                add(item2);
                add(item3);
                add(item4);
                add(item1);
            }
        };
        TopologicalSort ts = new TopologicalSort(list);
        List<IBootstrapItem<String>> result = ts.getOrderedList(false);
        assertEquals(result.get(0), item4);
        assertEquals(result.get(1), item5);
        assertEquals(result.get(2), item1);
        assertEquals(result.get(3), item2);
        assertEquals(result.get(4), item3);
        reset(item1);
        reset(item2);
        reset(item3);
        reset(item4);
        reset(item5);
    }

    @Test
    public void checkSortingTwoSimpleGraphsWithRevertedResult()
            throws Exception {
        IBootstrapItem<String> item1 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item2 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item3 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item4 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item5 = mock(IBootstrapItem.class);

        when(item1.getItemName()).thenReturn("item1");
        when(item2.getItemName()).thenReturn("item2");
        when(item3.getItemName()).thenReturn("item3");
        when(item4.getItemName()).thenReturn("item4");
        when(item5.getItemName()).thenReturn("item5");

        when(item1.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");}});
        when(item3.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item2");}});
        when(item4.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item5.getAfterItems()).thenReturn(new ArrayList<String>(){{}});

        when(item1.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item3.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});
        when(item4.getBeforeItems()).thenReturn(new ArrayList<String>(){{add("item5");}});
        when(item5.getBeforeItems()).thenReturn(new ArrayList<String>(){{}});

        List<IBootstrapItem<String>> list = new ArrayList<IBootstrapItem<String>>() {
            {
                add(item5);
                add(item2);
                add(item3);
                add(item4);
                add(item1);
            }
        };
        TopologicalSort ts = new TopologicalSort(list);
        List<IBootstrapItem<String>> result = ts.getOrderedList(true);
        assertEquals(result.get(0), item3);
        assertEquals(result.get(1), item2);
        assertEquals(result.get(2), item1);
        assertEquals(result.get(3), item5);
        assertEquals(result.get(4), item4);
        reset(item1);
        reset(item2);
        reset(item3);
        reset(item4);
        reset(item5);
    }

    @Test
    public void checkComplexGraph()
            throws Exception {
        IBootstrapItem<String> item1 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item2 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item3 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item4 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item5 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item6 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item7 = mock(IBootstrapItem.class);

        when(item1.getItemName()).thenReturn("item1");
        when(item2.getItemName()).thenReturn("item2");
        when(item3.getItemName()).thenReturn("item3");
        when(item4.getItemName()).thenReturn("item4");
        when(item5.getItemName()).thenReturn("item5");
        when(item6.getItemName()).thenReturn("item6");
        when(item7.getItemName()).thenReturn("item7");

        when(item1.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");}});
        when(item3.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");}});
        when(item4.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item3");}});
        when(item5.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item4");}});
        when(item6.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item3");add("item2");}});
        when(item7.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item6");add("item5");}});

        List<IBootstrapItem<String>> list = new ArrayList<IBootstrapItem<String>>() {
            {
                add(item5);
                add(item7);
                add(item2);
                add(item6);
                add(item3);
                add(item4);
                add(item1);
            }
        };

        TopologicalSort ts = new TopologicalSort(list);
        List<IBootstrapItem<String>> result = ts.getOrderedList(false);

        assertEquals(result.get(0), item1);
        assertEquals(result.get(1), item3);
        assertEquals(result.get(2), item4);
        assertEquals(result.get(3), item5);
        assertEquals(result.get(4), item2);
        assertEquals(result.get(5), item6);
        assertEquals(result.get(6), item7);

        reset(item1);
        reset(item2);
        reset(item3);
        reset(item4);
        reset(item5);
        reset(item6);
        reset(item7);
    }

    @Test (expected = Exception.class)
    public void checkOnRecursiveDependency()
            throws Exception {
        IBootstrapItem<String> item1 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item2 = mock(IBootstrapItem.class);

        when(item1.getItemName()).thenReturn("item1");
        when(item2.getItemName()).thenReturn("item2");

        when(item1.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item2");}});
        when(item2.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");}});

        List<IBootstrapItem<String>> list = new ArrayList<IBootstrapItem<String>>() {
            {
                add(item1);
                add(item2);
            }
        };
        TopologicalSort ts = new TopologicalSort(list);
        fail();
    }

    @Test (expected = Exception.class)
    public void checkOnAbsentDependency()
            throws Exception {
        IBootstrapItem<String> item1 = mock(IBootstrapItem.class);
        IBootstrapItem<String> item2 = mock(IBootstrapItem.class);

        when(item1.getItemName()).thenReturn("item1");
        when(item2.getItemName()).thenReturn("item2");

        when(item1.getAfterItems()).thenReturn(new ArrayList<String>(){{}});
        when(item2.getAfterItems()).thenReturn(new ArrayList<String>(){{add("item1");add("item3");}});

        List<IBootstrapItem<String>> list = new ArrayList<IBootstrapItem<String>>() {
            {
                add(item1);
                add(item2);
            }
        };
        TopologicalSort ts = new TopologicalSort(list);
        fail();
    }
}
