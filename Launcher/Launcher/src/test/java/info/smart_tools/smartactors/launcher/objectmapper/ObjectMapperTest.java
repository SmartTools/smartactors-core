package info.smart_tools.smartactors.launcher.objectmapper;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ObjectMapperInstanceException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ObjectMapperTest {

    private IObjectMapper objectMapper;

    @Before
    public void init() throws Exception {
        ClassLoader cl = mock(ClassLoader.class);
        Class<?> om = com.fasterxml.jackson.databind.ObjectMapper.class;
        Class<?> df = com.fasterxml.jackson.databind.DeserializationFeature.class;
        doReturn(om).when(cl).loadClass("com.fasterxml.jackson.databind.ObjectMapper");
        doReturn(df).when(cl).loadClass("com.fasterxml.jackson.databind.DeserializationFeature");
        this.objectMapper = new ObjectMapper(cl);
    }

    @Test
    public void testMapJsonToPOJO() throws Exception {
        String json = "{\"value\": \"hello\"}";
        ExampleModel model = objectMapper.readJson(json, ExampleModel.class);
        assertEquals("hello", model.getValue());
    }

    @Test(expected = ReadJsonException.class)
    public void testTryToMapNullValue() throws Exception {
        objectMapper.readJson(null, ExampleModel.class);
    }

    @Test(expected = ReadJsonException.class)
    public void testTryToMapJsonToNullClass() throws Exception {
        String json = "{\"value\": \"hello\"}";
        objectMapper.readJson(json, null);
    }

    @Test(expected = ObjectMapperInstanceException.class)
    public void testMissingClassesInClassLoader() throws Exception {
        ClassLoader cl = mock(ClassLoader.class);
        doThrow(new ClassNotFoundException()).when(cl).loadClass(anyString());
        new ObjectMapper(cl);
    }
}

class ExampleModel {
    private String value;

    public ExampleModel() {
        this.value = "";
    }

    public ExampleModel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
