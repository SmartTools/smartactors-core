package info.smart_tools.smartactors.iobject_extension.wds_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of {@link IObject} and {@link IObjectWrapper}
 */
public class WDSObject implements IObject, IObjectWrapper {
    private IObject initIObject;
    private WDSObjectFieldSet fieldSet;

    /**
     * Create empty instance of {@link WDSObject} by initialization instance of {@link IObject}
     * @param wrapperConfig the part of global config for current wrapper
     * @throws InvalidArgumentException if environment is null
     */
    public WDSObject(final IObject wrapperConfig)
            throws InvalidArgumentException {
        this(new WDSObjectFieldSet(wrapperConfig, new HashMap<>(), new HashMap<>()));
    }

    public WDSObject(final WDSObjectFieldSet fieldSet)
            throws InvalidArgumentException {
        this.fieldSet = fieldSet;
    }

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }

        return fieldSet.read(initIObject, name);
    }

    @Override
    public void setValue(final IFieldName name, final Object value)
            throws ChangeValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }

        fieldSet.write(initIObject, name, value);
    }

    @Override
    public void deleteField(final IFieldName name)
            throws DeleteValueException, InvalidArgumentException {
        throw new DeleteValueException("Method not implemented.");
    }

    @Override
    public <T> T serialize()
            throws SerializeException {
        throw new SerializeException("Method not implemented.");
    }

    @Override
    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        return null;
    }

    @Override
    public void init(final IObject environment) {
        this.initIObject = environment;
    }

    @Override
    public IObject getEnvironmentIObject(final IFieldName fieldName)
            throws InvalidArgumentException {
        if (null == fieldName) {
            throw new InvalidArgumentException("FieldName should not be null.");
        }
        try {
            return (IObject) this.initIObject.getValue(fieldName);
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not read data from environment with field name " + fieldName, e);
        }
    }
}
