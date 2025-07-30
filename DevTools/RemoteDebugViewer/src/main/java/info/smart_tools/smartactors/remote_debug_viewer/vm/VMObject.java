package info.smart_tools.smartactors.remote_debug_viewer.vm;

import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

import java.util.List;

public class VMObject {

    private final ObjectReference objectReference;
    private final ReferenceType referenceType;

    public VMObject(
            final ObjectReference objectReference,
            final ReferenceType referenceType
    ) {
        this.objectReference = objectReference;
        this.referenceType = referenceType;
    }

    public ObjectReference getObjectReference() {
        return objectReference;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public VMValue getFieldValue(final String fieldName) {
        Field keyField = referenceType.fieldByName(fieldName);
        if (keyField == null) {
            return null;
        }

        Value value = objectReference.getValue(keyField);
        return new VMValue(value);
    }

    public VMMethod getMethod(final String methodName) {
        List<Method> methods = referenceType.methodsByName(methodName);
        checkMethods(methods, methodName);
        Method method = methods.get(0);

        return new VMMethod(objectReference, method);
    }

    public VMMethod getMethod(final String methodName, final String methodSignature) {
        List<Method> methods = referenceType.methodsByName(methodName, methodSignature);
        checkMethods(methods, methodName);
        Method method = methods.get(0);

        return new VMMethod(objectReference, method);
    }

    private void checkMethods(final List<Method> methods, final String methodName) {
        if (methods.isEmpty()) {
            throw new RuntimeException("No matching method found for method \"" + methodName + "\".");
        }
        if (methods.size() > 1) {
            System.out.println("More than 1 method \"" + methodName + "\" exist. Retrieving the first one.");
        }
    }
}
