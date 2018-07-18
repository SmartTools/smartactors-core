package info.smart_tools.smartactors.endpoint_components_generic.handler_configuration_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class HandlerConfigurationObjectTest extends TrivialPluginsLoadingTestBase {
    private IObject iObjectMock, iObjectMock1;
    private IFieldName fieldNameMock;

    @Override
    protected void registerMocks() throws Exception {
        iObjectMock = mock(IObject.class);
        iObjectMock1 = mock(IObject.class);
        fieldNameMock = mock(IFieldName.class);
    }

    @Test public void Should_parseObjectsWithLinkedFields() throws Exception {
        IObject a = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                "   'd': 'theValue'," +
                "   'a': '@@b'" +
                "}").replace('\'','"'));
        IObject b = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                "   'c': '@@d'" +
                "}").replace('\'','"'));
        IObject c = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                "   'b': '@@c'" +
                "}").replace('\'','"'));

        IObject conf = new HandlerConfigurationObject(new IObject[] {a, b, c});

        IFieldName aFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "a");

        assertEquals("theValue", conf.getValue(aFN));
    }

    @Test public void Should_parseLinksToObjectItself() throws Exception {
        IObject a = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                ("{" +
                "   'b': 'theValue'," +
                "   'a': '@@b'" +
                "}").replace('\'','"'));

        IObject conf = new HandlerConfigurationObject(new IObject[] {a});

        IFieldName aFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "a");

        assertEquals("theValue", conf.getValue(aFN));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenEmptyArrayPassedToConstructor() throws Exception {
        new HandlerConfigurationObject(new IObject[0]);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenEmptyNullPassedToConstructor() throws Exception {
        new HandlerConfigurationObject(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void Should_notSupportIteration() throws Exception {
        new HandlerConfigurationObject(new IObject[] {iObjectMock}).iterator();
    }

    @Test(expected = SerializeException.class)
    public void Should_notSupportSerialization() throws Exception {
        try {
            new HandlerConfigurationObject(new IObject[]{iObjectMock}).serialize();
        } finally {
            verifyZeroInteractions(iObjectMock);
        }
    }

    @Test public void Should_writeValuesToFirstObject() throws Exception {
        IObject o = new HandlerConfigurationObject(new IObject[] {iObjectMock, iObjectMock1});

        o.setValue(fieldNameMock, "value");

        verify(iObjectMock).setValue(same(fieldNameMock), eq("value"));
        verifyZeroInteractions(iObjectMock1);
    }

    @Test public void Should_deleteValuesFromFirstObject() throws Exception {
        IObject o = new HandlerConfigurationObject(new IObject[] {iObjectMock, iObjectMock1});

        o.deleteField(fieldNameMock);

        verify(iObjectMock).deleteField(same(fieldNameMock));
        verifyZeroInteractions(iObjectMock1);
    }
}
