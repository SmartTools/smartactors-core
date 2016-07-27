package info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor summary info
 */
public class ConstructorInfo {

    private ClassBuilder builder;
    private Modifiers modifier;
    private List<ConstructorParameterInfo> parameters = new ArrayList<>();
    private List<String> exceptions = new ArrayList<>();
    private List<String> body = new ArrayList<>();

    /**
     * Constructor.
     * Create instance of {@link ConstructorInfo} by given {@link ClassBuilder}
     * @param builder the link to parent instance of {@link ClassBuilder}
     */
    public ConstructorInfo(final ClassBuilder builder) {
        this.builder = builder;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    /**
     * Set modifier of constructor
     * @param modifierOfClass the constructor modifier
     * @return current instance of {@link ConstructorInfo}
     */
    public ConstructorInfo setModifier(final Modifiers modifierOfClass) {
        this.modifier = modifierOfClass;

        return this;
    }

    public List<ConstructorParameterInfo> getParameters() {
        return parameters;
    }

    /**
     * Add new parameter to the current constructor and start editing arguments of new parameter
     * @return instance of {@link ConstructorParameterInfo}
     */
    public ConstructorParameterInfo setParameters() {
        ConstructorParameterInfo parameterInfo = new ConstructorParameterInfo(this);
        this.parameters.add(parameterInfo);

        return parameterInfo;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    /**
     * Add new exception to the current constructor by given exception name
     * @param exception the name of given exception
     * @return current instance of {@link ConstructorInfo}
     */
    public ConstructorInfo setExceptions(final String exception) {
        this.exceptions.add(exception);

        return this;
    }

    public List<String> getBody() {
        return body;
    }

    /**
     * Add next string to the constructor body
     * @param string the string with constructor code
     * @return current instance of {@link ConstructorInfo}
     */
    public ConstructorInfo addStringToBody(final String string) {
        this.body.add(string);

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link ClassBuilder}
     */
    public ClassBuilder next() {
        return this.builder;
    }
}
