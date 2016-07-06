package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Support class for generate wrappers
 * @param <T> type of returning value for 'out' method or type of value that will be put into iObject for 'in' method
 */
public class Field<T> {

    private static final String FIELD_TARGET = "target";
    private static final String FIELD_ARGS = "args";
    private static final String FIELD_NAME = "name";
    private static final String RULES_KEYWORD = "rules";
    private static final String METHOD_IN = "in";
    private static final String METHOD_OUT = "out";
    private static final String SPLITTER = "\\/";
    private static final String BINDING_KEYWORD = "binding";

    //private String bindingLocation;
    private List<Map<String, Object>> rules;

    /**
     * Constructor.
     * Create instance of {@link Field}
     * @param bindingPath the path to binding for current instance of {@link Field}
     */
    public Field(final String bindingPath) throws InvalidArgumentException {
        try {
            this.rules = (ArrayList<Map<String, Object>>) (
                    getValueFromNestedIObject(
                            IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), BINDING_KEYWORD),
                            bindingPath + "/" + RULES_KEYWORD
                    )
            );
        } catch (Throwable e) {
            throw new InvalidArgumentException("" ,e);
        }
    }

    /**
     * Apply rules and return result
     * @param env instance of {@link IObject} with data
     * @return instance of {@link T}
     * @throws ReadValueException if any errors occurred when iobject had been reading
     * @throws InvalidArgumentException if incoming arguments are incorrect
     * @throws ClassCastException when returning type doesn't match required type
     */
    public T out(final IObject env)
            throws ReadValueException, InvalidArgumentException, ClassCastException {
        if (null == env) {
            throw new InvalidArgumentException("Environment should not be null.");
        }
        Object value;
        Object out = null;
        try {
            for (Map<String, Object> rule : this.rules) {
                String target = (String) rule.get(FIELD_TARGET);
                List<String> args = (ArrayList<String>) rule.get(FIELD_ARGS);
                String name = (String) rule.get(FIELD_NAME);
                Object[] resolvedArgs = new Object[args.size()];
                for (String arg : args) {
                    resolvedArgs[args.indexOf(arg)] =  getValueFromNestedIObject(env, arg);
                }
                value = !name.isEmpty() ? IOC.resolve(Keys.getOrAdd(name), resolvedArgs) : resolvedArgs[0];
                if (!target.isEmpty() && !target.equals(METHOD_OUT)) {
                    setValueToNestedIObject(env, target, value);
                }
                if (target.equals(METHOD_OUT)) {
                    out = value;
                }
            }
        } catch (Exception e) {
            throw new ReadValueException("Could not apply rules.", e);
        }
        if (null == out) {
            return null;
        }

        return (T) out;
    }

    /**
     * Apply rules to given argument without return result
     * @param env instance of {@link IObject} with data
     * @param in given argument
     * @throws ChangeValueException if any errors occurred when iobject had been changing
     * @throws InvalidArgumentException if incoming arguments are incorrect
     */
    public void in(final IObject env, final T in)
            throws ChangeValueException, InvalidArgumentException {
        if (null == env) {
            throw new InvalidArgumentException("Environment should not be null.");
        }
        Object value;
        try {
            for (Map<String, Object> rule : this.rules) {
                String target = (String) rule.get(FIELD_TARGET);
                List<String> args = (ArrayList<String>) rule.get(FIELD_ARGS);
                String name = (String) rule.get(FIELD_NAME);
                Object[] resolvedArgs = new Object[args.size()];
                for (String arg : args) {
                    resolvedArgs[args.indexOf(arg)] = arg.equals(METHOD_IN) ? in : getValueFromNestedIObject(env, arg);
                }
                value = !name.isEmpty() ? IOC.resolve(Keys.getOrAdd(name), resolvedArgs) : resolvedArgs[0];
                if (!target.isEmpty()) {
                    setValueToNestedIObject(env, target, value);
                }
            }
        } catch (Exception e) {
            throw new ChangeValueException("Could not apply rules.", e);
        }
    }

    private Object getValueFromNestedIObject(final IObject source, final String location)
            throws ReadValueException {
        try {
            String[] separated = location.split(SPLITTER);
            return getNestedIObject(source, separated).getValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), separated[separated.length - 1])
            );
        } catch (Throwable e) {
            throw new ReadValueException("Could not read value from IObject", e);
        }
    }

    private void setValueToNestedIObject(final IObject source, final String location, final Object value)
            throws ChangeValueException {
        try {
            String[] separated = location.split(SPLITTER);
            getNestedIObject(source, separated).setValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), separated[separated.length - 1]),
                    value
            );
        } catch (Throwable e) {
            throw new ChangeValueException("Could not write value to IObject", e);
        }
    }

    private IObject getNestedIObject(final IObject source, final String[] separatedPath) throws Exception {
        try {
            IObject nestedObject = (IObject) source.getValue(
                    IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), separatedPath[0])
            );
            for (int i = 1; i < separatedPath.length - 1; ++i) {
                nestedObject = (IObject) nestedObject.getValue(
                        IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), separatedPath[i])
                );
            }

            return nestedObject;
        } catch (Throwable e) {
            throw new ReadValueException("Could not read nested IObject.", e);
        }
    }
}
