package info.smart_tools.smartactors.field.nested_field;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link IField}
 */
public class NestedField implements IField {
    private List<Field> steps = new LinkedList<>();
    private IField field;
    private static final String SPLITTER = "\\/";

    private IObject getFinalObjectWrite(IObject object)
            throws InvalidArgumentException {
        try {
            for (Field step : steps) {
                if (step.in(object) == null) {
                    step.out(
                            object,
                            IOC.resolve(
                                    Keys.getKeyByName(IObject.class.getCanonicalName())
                            )
                    );
                }

                object = step.in(object);
            }
            return object;
        } catch (Throwable e) {
            throw new InvalidArgumentException(e);
        }
    }

    private IObject getFinalObjectRead(IObject object)
            throws InvalidArgumentException {
        try {
            for (Field step : steps) {
                object = step.in(object, IObject.class);
            }
            return object;
        } catch (Throwable e) {
            throw new InvalidArgumentException(e);
        }
    }

    public NestedField(final String fieldName) throws InvalidArgumentException {
        try {
            String[] stepNames = fieldName.split(SPLITTER);

            field = new Field(
                    IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), stepNames[stepNames.length - 1])
            );

            for (int i = 0; i < stepNames.length - 1; ++i) {
                steps.add(
                        new Field(
                                IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), stepNames[i])
                        )
                );
            }
        } catch (Throwable e) {
            throw new InvalidArgumentException(e);
        }
    }

    @Override
    public <T> void out(final IObject env, final T in) throws ChangeValueException, InvalidArgumentException {
        field.out(getFinalObjectWrite(env), in);
    }

    @Override
    public <T> T in(final IObject env, final Class type) throws ReadValueException, InvalidArgumentException {
        return field.in(getFinalObjectRead(env), type);
    }

    @Override
    public <T> T in(final IObject env) throws ReadValueException, InvalidArgumentException {
        return field.in(getFinalObjectRead(env));
    }
}
