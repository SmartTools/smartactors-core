package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Class extends {@link SimpleJavaFileObject}
 */
class CompiledCode extends SimpleJavaFileObject {

    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    /**
     * Constructor
     * Creates instance of {@link CompiledCode} by given class name
     * @param className name of class
     * @throws Exception if any errors occurred
     */
    CompiledCode(final String className)
            throws Exception {
        super(new URI(className), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream()
            throws IOException {
        return stream;
    }

    byte[] getByteCode() {
        return stream.toByteArray();
    }
}
