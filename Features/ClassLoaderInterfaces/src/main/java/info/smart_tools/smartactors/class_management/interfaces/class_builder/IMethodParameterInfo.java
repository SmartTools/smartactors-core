package info.smart_tools.smartactors.class_management.interfaces.class_builder;

public interface IMethodParameterInfo {

    String getName();

    String getType();

    /**
     * Set name of current method argument
     * @param nameOfArgument the name of method argument
     * @return instance of {@link IMethodParameterInfo}
     */
    IMethodParameterInfo setName(final String nameOfArgument);

    /**
     * Set type of current method argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link IMethodParameterInfo}
     */
    IMethodParameterInfo setType(final String typeOfArgument);

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IMethodInfo}
     */
    IMethodInfo next();
}
