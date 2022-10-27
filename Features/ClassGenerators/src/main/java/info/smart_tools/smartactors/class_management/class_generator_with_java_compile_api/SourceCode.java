package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Class extends {@link SimpleJavaFileObject}
 */
class SourceCode extends SimpleJavaFileObject {
    private String contents = null;

    /**
     * Constructor.
     * Creates instance of {@link SourceCode} by given instances of
     * {@link String} as class name and {@link String} as contents.
     * @param className class name
     * @param contents contents
     * @throws Exception if any errors occurred
     */
    SourceCode(final String className, final String contents)
            throws Exception {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
    }

    /**
     * Return char sequence as code source
     * @param ignoreEncodingErrors ignore errors of encoding
     * @return char sequence
     */
    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
        return contents;
    }
}
