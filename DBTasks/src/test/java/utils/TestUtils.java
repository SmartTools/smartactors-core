package utils;

import java.lang.reflect.Field;

public class TestUtils {
    public static Object getValue(final Field[] fields, final Object obj, final String name)
            throws IllegalAccessException {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field.get(obj);
            }
        }

        return null;
    }
}
