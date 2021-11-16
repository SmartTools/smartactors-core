package info.smart_tools.smartactors.remote_debug_viewer.vm;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import info.smart_tools.smartactors.remote_debug_viewer.value_formatter.Formatter;

public class VMValue {

    private final Value value;

    public VMValue(final Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public VMObject toObject() {
        if (!(value instanceof ObjectReference)) {
            return null;
        }

        return new VMObject(
                (ObjectReference) value,
                ((ObjectReference) value).referenceType()
        );
    }

    public <T> T castTo(Class<?> clazz) {
        if (value == null) {
            return null;
        }
        return Formatter.format(this, clazz);
    }
}
