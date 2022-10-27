package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;

/**
 * Class extends StandardJavaFileManager
 */
class ExtendedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private CompiledCode compiledCode;
    private ClassLoader cl;

    /**
     * Constructor.
     * Creates instance of {@link ExtendedJavaFileManager} by given
     * instances of {@link JavaFileManager}, {@link CompiledCode}, {@link ClassLoader}
     * @param fileManager instance of {@link JavaFileManager}
     * @param compiledCode instance of {@link CompiledCode}
     * @param cl instance of {@link ClassLoader}
     */
    ExtendedJavaFileManager(
            final JavaFileManager fileManager,
            final CompiledCode compiledCode,
            final ClassLoader cl
    ) {
        super(fileManager);
        this.compiledCode = compiledCode;
        this.cl = cl;
    }

    /**
     * Get compiled code
     * @param location instance of {@link Location}
     * @param className full name of future class
     * @param kind instance of {@link Kind}
     * @param sibling ??
     * @return compiled code
     * @throws IOException if any errors occurred
     */
    public JavaFileObject getJavaFileForOutput(
            final Location location,
            final String className,
            final Kind kind,
            final FileObject sibling
    ) throws IOException {
        return this.compiledCode;
    }

    /**
     * Get current instance of {@link ClassLoader}
     * @param location instance of {@link Location}
     * @return instance of current {@link ClassLoader}
     */
    public ClassLoader getClassLoader(final Location location) {
        return this.cl;
    }
}

