package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IMethodInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IMethodParameterInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Method summary info
 */
public class MethodInfo implements IMethodInfo {

    private IClassBuilder builder;
    private Modifiers modifier;
    private String name;
    private List<IMethodParameterInfo> parameters = new ArrayList<>();
    private List<String> exceptions = new ArrayList<>();
    private List<String> body = new ArrayList<>();
    private String returnType;

    /**
     * Constructor.
     * Create new instance of {@link IMethodInfo} by given {@link IClassBuilder}
     * @param classBuilder the link to parent instance of {@link IClassBuilder}
     */
    public MethodInfo(final IClassBuilder classBuilder) {
        this.builder = classBuilder;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    /**
     * Set modifier of method
     * @param modifierOfMethod the method modifier
     * @return instance of {@link IMethodInfo}
     */
    public IMethodInfo setModifier(final Modifiers modifierOfMethod) {
        this.modifier = modifierOfMethod;

        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Set name of current method
     * @param nameOfMethod the name of method
     * @return instance of {@link IMethodInfo}
     */
    public IMethodInfo setName(final String nameOfMethod) {
        this.name = nameOfMethod;

        return this;
    }

    public List<IMethodParameterInfo> getParameters() {
        return parameters;
    }

    /**
     * Add new parameter to the current method and start editing arguments of new parameter
     * @return instance of {@link IMethodParameterInfo}
     */
    public IMethodParameterInfo addParameter() {
        IMethodParameterInfo parameterInfo = new MethodParameterInfo(this);
        this.parameters.add(parameterInfo);
        return parameterInfo;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    /**
     * Add new exception to the current method by given exception name
     * @param exception the name of given exception
     * @return current instance of {@link IMethodInfo}
     */
    public IMethodInfo setExceptions(final String exception) {
        this.exceptions.add(exception);

        return this;
    }

    public List<String> getBody() {
        return body;
    }

    /**
     * Add next string to the method body
     * @param string the string with constructor code
     * @return current instance of {@link IMethodInfo}
     */
    public IMethodInfo addStringToBody(final String string) {
        this.body.add(string);

        return this;
    }

    public String getReturnType() {
        return returnType;
    }

    /**
     * Set type of method return value
     * @param type the type of returning value
     * @return current instance of {@link IMethodInfo}
     */
    public IMethodInfo setReturnType(final String type) {
        this.returnType = type;

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
