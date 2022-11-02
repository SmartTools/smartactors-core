package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IMethodInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IMethodParameterInfo;

/**
 * Parameter summary class
 */
public class MethodParameterInfo implements IMethodParameterInfo {

    private IMethodInfo methodInfo;
    private String name;
    private String type;

    /**
     * Constructor.
     * Create new instance of {@link IMethodParameterInfo} by given {@link IMethodInfo}
     * @param methodInfo the link to parent instance of {@link IMethodInfo}
     */
    public MethodParameterInfo(final IMethodInfo methodInfo) {
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
     * @return instance of {@link IMethodParameterInfo}
     */
    public IMethodParameterInfo setName(final String nameOfArgument) {
        this.name = nameOfArgument;

        return this;
    }

    /**
     * Set type of current method argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link IMethodParameterInfo}
     */
    public IMethodParameterInfo setType(final String typeOfArgument) {
        this.type = typeOfArgument;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IMethodInfo}
     */
    public IMethodInfo next() {
        return this.methodInfo;
    }
}
