package info.smart_tools.smartactors.core.ds_object;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Tests for {@link FieldName}
 */
public class FieldNameTest {

    @Test
    public void checkCreation()
            throws Exception {
        IFieldName fieldName1 = new FieldName("test");
        IFieldName fieldName2 = new FieldName("test");
        IFieldName fieldNameOther = new FieldName("test_other");
        assertEquals(fieldName1, fieldName2);
        assertNotEquals(fieldName1, fieldNameOther);
        assertNotEquals(fieldName2, fieldNameOther);
    }

    @Test
    public void checkToString()
            throws Exception {
        String name = "test";
        IFieldName fieldName = new FieldName(name);
        assertEquals(name, fieldName.toString());
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkNullArgument()
            throws Exception {
        new FieldName(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkEmptyArgument()
            throws Exception {
        new FieldName("");
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkWrongArgument()
            throws Exception {
        new FieldName("?");
        fail();
    }
}
