package info.smart_tools.smartactors.class_management.interfaces.class_builder;

public interface IConstructorParameterInfo {

    String getName();

    String getType();

    /**
     * Set name of current constructor argument
     * @param nameOfArgument the name of constructor argument
     * @return instance of {@link IConstructorParameterInfo}
     */
    IConstructorParameterInfo setName(final String nameOfArgument);

    /**
     * Set type of current constructor argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link IConstructorParameterInfo}
     */
    public IConstructorParameterInfo setType(final String typeOfArgument);

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IConstructorInfo}
     */
    IConstructorInfo next();
}
