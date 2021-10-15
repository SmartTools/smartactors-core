package info.smart_tools.smartactors.ioc_viewer.value_formatter;

import com.sun.jdi.ArrayReference;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayValueFormatter implements IValueFormatter {

    @Override
    public <T> T format(VMValue value) {
        if (!(value.getValue() instanceof ArrayReference)) {
            throw new RuntimeException("Value is not ArrayReference, but " + value.getValue());
        }

        ArrayReference arrayReference = (ArrayReference) value.getValue();
        return (T) arrayReference.getValues().stream()
                .filter(Objects::nonNull)
                .map(VMValue::new)
                .collect(Collectors.toList());
    }
}
