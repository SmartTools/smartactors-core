package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class for compile string with java code to byte code
 * in memory
 *
 * @since 1.8
 */
class JavacToMemoryCodeCompiler {

    /**
     * System java compiler
     */
    private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    /**
     * Constructor.
     * Creates instance of {@link JavacToMemoryCodeCompiler}
     */
    private JavacToMemoryCodeCompiler() {}

    /**
     * Compile {@link String} with custom class to java byte code and represent
     * compiled class
     * @param className full name of future class
     * @param classSourceCode code source
     * @param classLoader instance of {@link ClassLoader} to put compiled code to
     * @return compiled class
     * @throws Exception if any errors occurred
     */
    @SuppressWarnings("unchecked")
    static synchronized Class<?> compile(
            final String className,
            final String classSourceCode,
            final ClassLoader classLoader
    )
            throws Exception {
        ISmartactorsClassLoader cl;
        if (null == classLoader || !(classLoader instanceof ISmartactorsClassLoader)) {
            throw new RuntimeException("Class loader must be not null and must be an instance of ISmartactorsClassLoader!");
        } else {
            cl = (ISmartactorsClassLoader) classLoader;
        }
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            // TODO: Empty catch block
        }
        try {
            List<String> optionList = new ArrayList<>(Arrays.asList("-classpath", getClassPath(cl)));
            SourceCode sourceCode = new SourceCode(className, classSourceCode);
            CompiledCode compiledCode = new CompiledCode(className);
            List compilationUnits = Collections.singletonList(sourceCode);
            ExtendedJavaFileManager fileManager = new ExtendedJavaFileManager(
                    javac.getStandardFileManager(null, null, null),
                    compiledCode,
                    cl.getCompilationClassLoader()
            );
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            CompilationTask task = javac.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    optionList,
                    null,
                    compilationUnits
            );
            if (!task.call()) {
                StringBuilder s = new StringBuilder();
                for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                    s
                            .append("\n")
                            .append(diagnostic);
                }
                throw new Exception("Failed to compile " + className + s.toString());

            }
            return cl.addClass(compiledCode.getName(), compiledCode.getByteCode());

        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     * Return all class paths as instance of {@link String} form given instance of {@link ISmartactorsClassLoader} and its dependencies
     * @param classLoader instance of {@link ISmartactorsClassLoader}
     * @return all class paths taken from {@link ISmartactorsClassLoader} and its dependencies
     */
    private static String getClassPath(final ISmartactorsClassLoader classLoader) {
        StringBuilder buf = new StringBuilder();
        String separator = System.getProperty("path.separator");
        buf.append(".");
        URL[] urls = classLoader.getURLsFromDependencies();
        for (URL url : urls) {
            String jarPathName = url.getFile();
            if (jarPathName.startsWith("file:")) {
                jarPathName = jarPathName.substring(
                        jarPathName.indexOf("file:") + "file:".length(), jarPathName.indexOf("!/")
                );
            }
            buf.append(separator).append(jarPathName);
        }
        return buf.toString();
    }
}
