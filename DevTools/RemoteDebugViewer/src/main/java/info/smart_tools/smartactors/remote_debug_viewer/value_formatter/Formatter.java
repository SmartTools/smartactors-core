package info.smart_tools.smartactors.remote_debug_viewer.value_formatter;

import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Formatter {

    private static final Map<Class<?>, IValueFormatter> formatterMap = new HashMap<>();

    static {
        formatterMap.put(String.class, new StringValueFormatter());
        formatterMap.put(List.class, new ArrayValueFormatter());
    }

    public static <T> T format(final VMValue value, final Class<?> clazz) {
        IValueFormatter formatter = formatterMap.get(clazz);
        if (formatter == null) {
            throw new RuntimeException("No formatter found for class " + clazz);
        }

        return formatter.format(value);
    }
}
