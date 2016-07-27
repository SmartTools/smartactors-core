package info.smart_tools.smartactors.core.receiver_generator;

import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;

import java.util.Iterator;
import java.util.Map;

public class CustomWrapper implements IObjectWrapper, ICustomWrapper, IObject {

    private IObject env;
    private Boolean getterUsed = false;
    private Boolean setterUsed = false;

    @Override
    public void init(IObject environment) {
        this.env = environment;
    }

    @Override
    public IObject getEnvironmentIObject(IFieldName fieldName) throws InvalidArgumentException {
        return null;
    }

    @Override
    public Integer getIntValue()
            throws ReadValueException {
        try {
            this.getterUsed = true;
            return 1;
        } catch (Throwable e) {
            throw new ReadValueException("", e);
        }
    }

    @Override
    public void setIntValue(Integer i) throws ChangeValueException {
        try {
            this.setterUsed = true;
        } catch (Throwable e) {
            throw new ChangeValueException("", e);
        }
    }

    public Boolean getGetterUsed() {
        return getterUsed;
    }

    public Boolean getSetterUsed() {
        return setterUsed;
    }

    public void setGetterUsed(Boolean getterUsed) {
        this.getterUsed = getterUsed;
    }

    public void setSetterUsed(Boolean setterUsed) {
        this.setterUsed = setterUsed;
    }

    public Object getValue(IFieldName name) throws ReadValueException, InvalidArgumentException {
        return null;
    }

    public void setValue(IFieldName name, Object value) throws ChangeValueException, InvalidArgumentException {

    }

    public void deleteField(IFieldName name) throws DeleteValueException, InvalidArgumentException {
        throw new DeleteValueException("Method not implemented.");
    }

    public <T> T serialize() throws SerializeException {
        throw new SerializeException("Method not implemented.");
    }

    public Iterator<Map.Entry<IFieldName, Object>> iterator() {
        return null;
    }
}
