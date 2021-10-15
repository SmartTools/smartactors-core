package info.smart_tools.smartactors.ioc_viewer.vm;

import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;

import java.util.List;

public class VMMethod {

    private final ObjectReference objectReference;
    private final Method method;

    public VMMethod(final ObjectReference objectReference, final Method method) {
        this.objectReference = objectReference;
        this.method = method;
    }

    public ObjectReference getObjectReference() {
        return objectReference;
    }

    public Method getMethod() {
        return method;
    }

    public VMValue invoke(final VMThread thread, final List<? extends Value> arguments, int options) {
        try {
            Value value = objectReference.invokeMethod(thread.getThreadReference(), method, arguments, options);

            return new VMValue(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute method", e);
        }
    }
}
