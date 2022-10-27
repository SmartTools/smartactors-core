package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.class_management.interfaces.iclass_generator.exception.ClassGenerationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link IClassGenerator}
 */
public class FromStringClassGenerator implements IClassGenerator<String> {

    /**
     * Constructor.
     * Creates new instance of {@link FromStringClassGenerator}
     */
    public FromStringClassGenerator() {}

    @Override
    public Class<?> generate(final String source, ClassLoader classLoader)
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
            Class<?> compiledClass = JavacToMemoryCodeCompiler.compile(fullClassName, source, classLoader);
            return compiledClass;
        } catch (Throwable e) {
            throw new ClassGenerationException("Could not generate class.", e);
        }
    }

    private String getFirstSubstringByPattern(final String source, final Pattern pattern) {
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }
}