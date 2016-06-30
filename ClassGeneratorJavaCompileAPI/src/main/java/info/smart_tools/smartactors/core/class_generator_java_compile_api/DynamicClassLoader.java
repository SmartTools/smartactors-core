package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class extends {@link ClassLoader}
 */
class DynamicClassLoader extends ClassLoader {

    private Map<String, CompiledCode> compiledCodeStorage = new HashMap<>();

    /**
     * Constructor.
     * Creates instance of {@link DynamicClassLoader} by given parent class loader
     * @param parent parent class loader
     */
    DynamicClassLoader(final ClassLoader parent) {
        super(parent);
    }

    /**
     * Add instance of {@link CompiledCode} to the local storage
     * @param cc instance of {@link CompiledCode}
     */
    void setCode(final CompiledCode cc) {
        compiledCodeStorage.put(cc.getName(), cc);
    }

    @Override
    protected Class<?> findClass(final String name)
            throws ClassNotFoundException {
        CompiledCode cc = compiledCodeStorage.get(name);
        if (cc == null) {
            return super.findClass(name);
        }
        byte[] byteCode = cc.getByteCode();
        return defineClass(name, byteCode, 0, byteCode.length);
    }
}