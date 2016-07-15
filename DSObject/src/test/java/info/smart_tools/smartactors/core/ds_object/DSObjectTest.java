package info.smart_tools.smartactors.core.ds_object;

import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests for DSObject
 */
public class DSObjectTest {

    @Test
    public void checkCreationByEmptyConstructor() {
        IObject obj = new DSObject();
        assertNotNull(obj);
    }

    @Test
    public void checkCreationByString()
            throws Exception {
        IObject obj = new DSObject("{\n" +
                "  \"value\": 1,\n" +
                "  \"string\": \"foo\"\n" +
                "}");
        assertNotNull(obj);
        assertEquals(1, obj.getValue(new FieldName("value")));
        assertEquals("foo", obj.getValue(new FieldName("string")));
    }

    @Test
    public void checkCreationByMap()
            throws Exception {
        IFieldName fieldName = mock(IFieldName.class);
        Object obj = mock(Object.class);
        Map<IFieldName, Object> map = new HashMap<IFieldName, Object>(){{put(fieldName, obj);}};
        IObject result = new DSObject(map);
        assertNotNull(obj);
        assertEquals(obj, result.getValue(fieldName));
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreationByString()
            throws Exception {
        String str = null;
        new DSObject(str);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnCreationByMap()
            throws Exception {
        Map<IFieldName, Object> map = null;
        new DSObject(map);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnGetValue()
            throws Exception {
        (new DSObject()).getValue(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnSetValue()
            throws Exception {
        (new DSObject()).setValue(null, new Object());
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkExceptionOnDeleteValue()
            throws Exception {
        (new DSObject()).deleteField(null);
        fail();
    }

    @Test
    public void checkSetGetAndDeleteValue()
            throws Exception {
        IFieldName fieldName = mock(IFieldName.class);
        Object value = mock(Object.class);
        IObject obj = new DSObject();
        assertNull(obj.getValue(fieldName));
        obj.setValue(fieldName, value);
        assertEquals(value, obj.getValue(fieldName));
        obj.deleteField(fieldName);
        assertNull(obj.getValue(fieldName));
    }

    @Test
    public void checkSerialization()
            throws Exception {
        String json = "{\"value\":1,\"string\":\"foo\"}";
        IObject obj = new DSObject(json);
        assertEquals(json, obj.serialize());
    }

    @Test (expected = SerializeException.class)
    public void checkExceptionOnSerialization()
            throws Exception {
        String json = "{\"value\":1,\"string\":\"foo\"}";
        IFieldName fieldName = mock(IFieldName.class);
        IObject obj = new DSObject(json);
        obj.setValue(fieldName, Thread.currentThread());
        obj.serialize();
        fail();
    }

    @Test
    public void checkSerializationWithNestedIObject()
            throws Exception {
        IObject obj = new DSObject();
        IFieldName fieldName = mock(IFieldName.class);
        obj.setValue(fieldName, new DSObject());
        String result = obj.serialize();
        assertNotNull(result);
    }

    @Test
    public void checkIterator()
            throws Exception {
        String json = "{\"value\":1,\"string\":\"foo\"}";
        IObject iObject = new DSObject(json);
        Iterator<Map.Entry<IFieldName, Object>> it = iObject.iterator();
        int index = 0;
        List<IFieldName> resultFieldName = new ArrayList<>();
        List<Object> resultValue = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<IFieldName, Object> obj = it.next();
            resultFieldName.add(obj.getKey());
            resultValue.add(obj.getValue());
            ++index;
        }
        assertEquals(index, 2);
        assertEquals(resultFieldName.get(0), new FieldName("value"));
        assertEquals(resultFieldName.get(1), new FieldName("string"));
        assertEquals(resultValue.get(0), 1);
        assertEquals(resultValue.get(1), "foo");
    }

    @Test
    public void checkIteratorInForeach()
            throws Exception {
        String json = "{\"value\":1,\"string\":\"foo\"}";
        IObject iObject = new DSObject(json);
        Iterator<Map.Entry<IFieldName, Object>> it = iObject.iterator();
        List<IFieldName> resultFieldName = new ArrayList<>();
        List<Object> resultValue = new ArrayList<>();
        for(Map.Entry<IFieldName, Object> entry : iObject) {
            resultFieldName.add(entry.getKey());
            resultValue.add(entry.getValue());
        }
        assertEquals(resultFieldName.get(0), new FieldName("value"));
        assertEquals(resultFieldName.get(1), new FieldName("string"));
        assertEquals(resultValue.get(0), 1);
        assertEquals(resultValue.get(1), "foo");
    }

    @Test
    public void checkHashCode() throws Exception {
        IObject fObject = new DSObject();
        IObject sObject = new DSObject();
        IObject thObject = new DSObject();
        IObject fthObject = new DSObject();
        IObject fveObject = new DSObject();
        IObject sxObject = new DSObject();
        IObject senObject = new DSObject();
        IObject eObject = new DSObject();

        IFieldName aFN = new FieldName("a");
        IFieldName bFN = new FieldName("b");
        IFieldName cFN = new FieldName("c");

        fObject.setValue(aFN, 1);
        fObject.setValue(bFN, fthObject);
        fthObject.setValue(cFN, 2);

        sObject.setValue(aFN, 1);
        sObject.setValue(bFN, fveObject);
        fveObject.setValue(cFN, 2);

        thObject.setValue(aFN, 1);
        thObject.setValue(bFN, sxObject);
        sxObject.setValue(cFN, 3);

        senObject.setValue(aFN, 1);
        senObject.setValue(cFN, eObject);
        eObject.setValue(cFN, 2);

        assertEquals(fObject.hashCode(), fObject.hashCode());
        assertEquals(fObject.hashCode(), sObject.hashCode());
        assertNotEquals(fObject.hashCode(), thObject.hashCode());
        assertNotEquals(fObject.hashCode(), senObject.hashCode());
        assertNotEquals(thObject.hashCode(), senObject.hashCode());
    }
}
