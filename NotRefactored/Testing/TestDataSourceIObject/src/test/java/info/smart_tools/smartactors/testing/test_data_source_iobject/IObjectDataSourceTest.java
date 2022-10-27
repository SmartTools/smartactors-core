package info.smart_tools.smartactors.testing.test_data_source_iobject;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link IObjectDataSource}.
 */
public class IObjectDataSourceTest {

    @Test
    public void checkCreation()
            throws Exception {
        ISource<IObject, IObject> source = new IObjectDataSource();
        assertNotNull(source);
    }

    @Test
    public void checkSetMethodAndCheckQueue()
            throws Exception {
        IObject testObject = mock(IObject.class);
        ISource<IObject, IObject> source = new IObjectDataSource();
        source.setSource(testObject);
        IObject result = source.next();
        assertNotNull(result);
        assertEquals(result, testObject);
    }

}
