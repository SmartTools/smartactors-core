package info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder;

/**
 * Parameter summary class
 */
public class MethodParameterInfo {

    private MethodInfo methodInfo;
    private String name;
    private String type;

    /**
     * Constructor.
     * Create new instance of {@link MethodParameterInfo} by given {@link MethodInfo}
     * @param methodInfo the link to parent instance of {@link MethodInfo}
     */
    public MethodParameterInfo(final MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    /**
     * Set name of current method argument
     * @param nameOfArgument the name of method argument
     * @return instance of {@link MethodParameterInfo}
     */
    public MethodParameterInfo setName(final String nameOfArgument) {
        this.name = nameOfArgument;

        return this;
    }

    /**
     * Set type of current method argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link MethodParameterInfo}
     */
    public MethodParameterInfo setType(final String typeOfArgument) {
        this.type = typeOfArgument;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link MethodInfo}
     */
    public MethodInfo next() {
        return this.methodInfo;
    }
}
