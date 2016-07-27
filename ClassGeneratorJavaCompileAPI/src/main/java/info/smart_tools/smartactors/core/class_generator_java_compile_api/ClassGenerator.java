package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import info.smart_tools.smartactors.core.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.core.iclass_generator.exception.ClassGenerationException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link IClassGenerator}
 */
public class ClassGenerator implements IClassGenerator<String> {

    private InMemoryCodeCompiler compiler;

    /**
     * Constructor.
     * Creates new instance of {@link ClassGenerator} by given class loader
     * @param classLoader instance of {@link ClassLoader}
     */
    public ClassGenerator(final ClassLoader classLoader) {
        compiler = new InMemoryCodeCompiler(classLoader);
    }

    @Override
    public Class<?> generate(final String source)
            throws ClassGenerationException, InvalidArgumentException {

        if (null == source || source.isEmpty()) {
            throw new InvalidArgumentException("Source code should not be null or empty.");
        }
        String packageName = getFirstSubstringByPattern(source, Pattern.compile("package\\s+([\\w\\.]+)"));
        String className = getFirstSubstringByPattern(source, Pattern.compile("(?:class|interface)\\s+(\\w+)"));
        if (null == packageName || packageName.isEmpty()) {
            throw new InvalidArgumentException("Source code doesn't contain package name.");
        }
        if (null == className || className.isEmpty()) {
            throw new InvalidArgumentException("Source code doesn't contain class.");
        }
        try {
            String fullClassName = packageName + "." + className;
            Class<?> compiledClass = this.compiler.compile(fullClassName, source);
            return compiledClass;
        } catch (Throwable e) {
            throw new ClassGenerationException("Could not generate class.", e);
        }
    }

    private String getFirstSubstringByPattern(final String source, final Pattern pattern) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}