package info.smart_tools.smartactors.testing.test_data_source_string_array;

import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link StringArrayDataSource}.
 */
public class StringArrayDataSourceTest {

    @Test
    public void checkCreation()
            throws Exception {
        ISource<String[], IObject> source = new StringArrayDataSource();
        assertNotNull(source);
    }

    @Test
    public void checkSetMethodAndCheckQueue()
            throws Exception {
        String[] data = new String[] {
                "{\"a\": \"a\"}",
                "{\"a\": \"b\"}",
                "{\"a\": \"c\"}"
        };

        ISource<String[], IObject> source = new StringArrayDataSource();
        source.setSource(data);
        IObject first = source.next();
        IObject second = source.next();
        IObject third = source.next();
        assertNotNull(first);
        assertEquals(first.getValue(new FieldName("a")), "a");
        assertEquals(second.getValue(new FieldName("a")), "b");
        assertEquals(third.getValue(new FieldName("a")), "c");
    }

    @Test
    public void checkSourceExtractionOnDataWithBrokenElement()
            throws Exception {
        String[] data = new String[] {
                "{a}",
                "{\"a\": \"a\"}"
        };
        ISource<String[], IObject> source = new StringArrayDataSource();
        String[] brokenItems = source.setSource(data);
        IObject first = source.next();
        assertEquals(first.getValue(new FieldName("a")), "a");
        assertEquals(brokenItems.length, 1);
        assertEquals(brokenItems[0], "{a}");
    }
}
