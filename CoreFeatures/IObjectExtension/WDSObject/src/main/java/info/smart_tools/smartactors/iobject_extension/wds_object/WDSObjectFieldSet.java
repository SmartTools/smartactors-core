package info.smart_tools.smartactors.iobject_extension.wds_object;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;
import java.util.Map;

/**
 * Field set encapsulates set of fields ({@link WDSObjectField WDSObjectField's}) configured by the one wrapper configuration object.
 */
public class WDSObjectFieldSet {
    private final IObject wrapperConfig;
    private final Map<IFieldName, IField> inFields;
    private final Map<IFieldName, IField[]> outFields;

    /**
     * The constructor.
     *
     * @param config          wrapper configuration object
     * @param inFieldsMap     map to store input fields in
     * @param outFieldsMap    map to store output fields in
     */
    public WDSObjectFieldSet(final IObject config, final Map<IFieldName, IField> inFieldsMap, final Map<IFieldName, IField[]> outFieldsMap) {
        this.wrapperConfig = config;
        this.inFields = inFieldsMap;
        this.outFields = outFieldsMap;
    }

    /**
     * Read a value from the object.
     *
     * @param env          the object to read from
     * @param fieldName    name of configured field to read
     * @return the value
     * @throws ReadValueException if any error occurs
     * @throws InvalidArgumentException if arguments are not valid
     */
    public Object read(final IObject env, final IFieldName fieldName) throws ReadValueException, InvalidArgumentException {
        IField field = inFields.get(fieldName);
        if (null == field) {
            try {
                field = new WDSObjectField((List<IObject>) this.wrapperConfig.getValue(fieldName));
            } catch (Throwable e) {
                throw new ReadValueException("Can't read configuration for current field name " + fieldName);
            }
            inFields.put(fieldName, field);
        }
        return field.in(env);
    }

    /**
     * Write a value to the object.
     *
     * @param env          the object to write value to
     * @param fieldName    name of the configured field to write
     * @param value        the value to write
     * @throws ChangeValueException if any error occurs
     * @throws InvalidArgumentException if arguments are not valid
     */
    public void write(final IObject env, final IFieldName fieldName, Object value) throws ChangeValueException, InvalidArgumentException {
        IField[] fields = outFields.get(fieldName);
        if (null == fields) {
            try {
                Object config = this.wrapperConfig.getValue(fieldName);
                fields = new IField[((List) config).size()];
                for (int i = 0; i < fields.length; ++i) {
                    fields[i] = new WDSObjectField((List<IObject>) ((List) config).get(i));
                }
            } catch (Throwable e) {
                throw new ChangeValueException("Can't read configuration for current field name " + fieldName);
            }
            outFields.put(fieldName, fields);
        }
        for (IField f : fields) {
            f.out(env, value);
        }
    }
}
