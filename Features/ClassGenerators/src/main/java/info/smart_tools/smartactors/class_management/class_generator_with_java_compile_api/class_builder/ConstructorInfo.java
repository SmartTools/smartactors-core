package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IConstructorInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IConstructorParameterInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor summary info
 */
public class ConstructorInfo implements IConstructorInfo {

    private IClassBuilder builder;
    private Modifiers modifier;
    private List<IConstructorParameterInfo> parameters = new ArrayList<>();
    private List<String> exceptions = new ArrayList<>();
    private List<String> body = new ArrayList<>();

    /**
     * Constructor.
     * Create instance of {@link ConstructorInfo} by given {@link IClassBuilder}
     * @param builder the link to parent instance of {@link IClassBuilder}
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
     * @return current instance of {@link IConstructorInfo}
     */
    public IConstructorInfo setModifier(final Modifiers modifierOfClass) {
        this.modifier = modifierOfClass;

        return this;
    }

    public List<IConstructorParameterInfo> getParameters() {
        return parameters;
    }

    /**
     * Add new parameter to the current constructor and start editing arguments of new parameter
     * @return instance of {@link IConstructorParameterInfo}
     */
    public IConstructorParameterInfo setParameters() {
        IConstructorParameterInfo parameterInfo = new ConstructorParameterInfo(this);
        this.parameters.add(parameterInfo);

        return parameterInfo;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    /**
     * Add new exception to the current constructor by given exception name
     * @param exception the name of given exception
     * @return current instance of {@link IConstructorInfo}
     */
    public IConstructorInfo setExceptions(final String exception) {
        this.exceptions.add(exception);

        return this;
    }

    public List<String> getBody() {
        return body;
    }

    /**
     * Add next string to the constructor body
     * @param string the string with constructor code
     * @return current instance of {@link IConstructorInfo}
     */
    public IConstructorInfo addStringToBody(final String string) {
        this.body.add(string);

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    public IClassBuilder next() {
        return this.builder;
    }
}
