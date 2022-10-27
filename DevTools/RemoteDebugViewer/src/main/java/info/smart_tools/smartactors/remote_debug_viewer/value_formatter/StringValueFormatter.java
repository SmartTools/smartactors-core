package info.smart_tools.smartactors.remote_debug_viewer.value_formatter;

import com.sun.jdi.StringReference;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

public class StringValueFormatter implements IValueFormatter {

    @Override
    public <T> T format(final VMValue value) {
        if (!(value.getValue() instanceof StringReference)) {
            throw new RuntimeException("Value is not StringReference, but " + value.getValue());
        }

        return (T) ((StringReference) value.getValue()).value();
    }
}
