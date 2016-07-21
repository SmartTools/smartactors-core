package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
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
class InMemoryCodeCompiler {

    /**
     * System java compiler
     */
    private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private DynamicClassLoader classLoader;

    /**
     * Constructor.
     * Creates instance of {@link InMemoryCodeCompiler} by given class loader
     * @param classLoader instance of {@link ClassLoader}
     */
    InMemoryCodeCompiler(final ClassLoader classLoader) {
        this.classLoader = new DynamicClassLoader(
                null != classLoader ? classLoader : ClassLoader.getSystemClassLoader()
        );
    }

    /**
     * Compile {@link String} with custom class to java byte code and represent
     * compiled class
     * @param className full name of future class
     * @param sourceCodeInText code source
     * @return compiled class
     * @throws Exception if any errors occurred
     */
    Class<?> compile(
            final String className,
            final String sourceCodeInText
    )
            throws Exception {
        try {
            return this.classLoader.loadClass(className);
        } catch (ClassNotFoundException e) { }
        try {
            List<String> optionList = new ArrayList<>();
            if (null != this.classLoader) {
                optionList.addAll(Arrays.asList("-classpath", getClassPath(this.classLoader)));
            }
            SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
            CompiledCode compiledCode = new CompiledCode(className);
            List compilationUnits = Collections.singletonList(sourceCode);
            ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(
                    javac.getStandardFileManager(null, null, null), compiledCode, this.classLoader
            );

            CompilationTask task = javac.getTask(
                    null,
                    fileManager,
                    null,
                    optionList,
                    null,
                    compilationUnits
            );
            task.call();
            return this.classLoader.loadClass(className);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     * Return all class paths as instance of {@link String} form given instance of {@link ClassLoader}
     * @param classLoader instance of {@link ClassLoader}
     * @return all class paths
     */
    private static String getClassPath(final ClassLoader classLoader) {
        ClassLoader cl = classLoader;
        StringBuilder buf = new StringBuilder();
        buf.append(".");
        String separator = System.getProperty("path.separator");
        while (null != cl) {
            try {
                URLClassLoader ucl = (URLClassLoader) cl;

                URL[] urls = ucl.getURLs();
                for (URL url : urls) {
                    String jarPathName = url.getFile();
                    if (jarPathName.startsWith("file:")) {
                        jarPathName = jarPathName.substring(
                                jarPathName.indexOf("file:") + "file:".length(), jarPathName.indexOf("!/")
                        );
                    }
                    buf.append(separator).append(
                            jarPathName
                    );
                }
            } catch (Exception e) {
                // do nothing
                // because this try-catch check cast ClassLoader to URLClassLoader
            }
            cl = cl.getParent();
        }

        return buf.toString();
    }
}
