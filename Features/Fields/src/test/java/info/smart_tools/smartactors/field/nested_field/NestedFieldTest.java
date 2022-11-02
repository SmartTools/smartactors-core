package info.smart_tools.smartactors.field.nested_field;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NestedFieldTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Test
    public void Should_ReturnNestedFieldValue() throws Exception {
//        IObject object = new DSObject("{\"fieldA\":{\"fieldB\":\"I am nested field\"}}");
        IObject object = IOC.resolve(
                Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{\"fieldA\":{\"fieldB\":\"I am nested field\"}}"
        );
        NestedField nestedField = new NestedField("fieldA/fieldB");

        assertEquals(nestedField.in(object, String.class), "I am nested field");
    }

    @Test
    public void Should_SetNestedFieldValue() throws Exception {
//        IObject object = new DSObject("{\"fieldA\":{\"fieldB\":{}}}");
        IObject object = IOC.resolve(
                Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{\"fieldA\":{\"fieldB\":{}}}"
        );

        NestedField nestedField = new NestedField("fieldA/fieldB/fieldC");

        nestedField.out(object, "FIELD");

        assertEquals(nestedField.in(object), "FIELD");
    }

    @Test
    public void Should_SetNestedFieldValue_when_field_is_not_set() throws Exception {
//        IObject object = new DSObject("{\"fieldA\":{}}}");
        IObject object = IOC.resolve(
                Keys.getKeyByName(IObject.class.getCanonicalName()),
                "{\"fieldA\":{}}}"
        );

        NestedField nestedField = new NestedField("fieldA/fieldB/fieldC");

        nestedField.out(object, "FIELD");

        assertEquals(nestedField.in(object), "FIELD");
    }
}
