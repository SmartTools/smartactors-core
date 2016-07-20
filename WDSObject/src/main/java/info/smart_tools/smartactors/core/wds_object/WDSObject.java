package info.smart_tools.smartactors.core.wds_object;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IObject} and {@link IObjectWrapper}
 */
public class WDSObject implements IObject, IObjectWrapper {

    private IObject initIObject;
    private Map<IFieldName, IField> inFields;
    private Map<IFieldName, IField[]> outFields;
    private IObject wrapperConfig;

    /**
     * Create empty instance of {@link WDSObject} by initialization instance of {@link IObject}
     * @param wrapperConfig the part of global config for current wrapper
     * @throws InvalidArgumentException if environment is null
     */
    public WDSObject(final IObject wrapperConfig)
            throws InvalidArgumentException {
        this.inFields = new HashMap<>(0);
        this.outFields = new HashMap<>(0);
        this.wrapperConfig = wrapperConfig;
    }

    @Override
    public Object getValue(final IFieldName name)
            throws ReadValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        IField field = inFields.get(name);
        if (null == field) {
            try {
                field = new WDSObjectField((List<IObject>) this.wrapperConfig.getValue(name));
            } catch (Throwable e) {
                throw new ReadValueException("Can't read configuration for current field name");
            }
            inFields.put(name, field);
        }
        return field.in(this.initIObject);
    }

    @Override
    public void setValue(final IFieldName name, final Object value)
            throws ChangeValueException, InvalidArgumentException {
        if (null == name) {
            throw new InvalidArgumentException("Name parameter should not be null.");
        }
        IField[] fields = outFields.get(name);
        if (null == fields) {
            try {
                Object config = this.wrapperConfig.getValue(name);
                fields = new IField[((List<List<IObject>>) config).size()];
                for (int i = 0; i < fields.length; ++i) {
                    fields[i] = new WDSObjectField(((List<List<IObject>>) config).get(i));
                }
            } catch (Throwable e) {
                throw new ChangeValueException("Can't read configuration for current field name");
            }
            outFields.put(name, fields);
        }
        for (IField f : fields) {
            f.out(this.initIObject, value);
        }
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
            throw new InvalidArgumentException("Could not read data from environment.", e);
        }
    }
}
