package info.smart_tools.smartactors.core.wrapper_generator.class_builder;

/**
 * Parameter summary class
 */
public class ConstructorParameterInfo {

    private ConstructorInfo constructorInfo;
    private String name;
    private String type;

    /**
     * Constructor.
     * Create new instance of {@link ConstructorParameterInfo} by given {@link ConstructorInfo}
     * @param constructorInfo the link to parent instance of {@link ConstructorInfo}
     */
    public ConstructorParameterInfo(final ConstructorInfo constructorInfo) {
        this.constructorInfo = constructorInfo;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    /**
     * Set name of current constructor argument
     * @param nameOfArgument the name of constructor argument
     * @return instance of {@link ConstructorParameterInfo}
     */
    public ConstructorParameterInfo setName(final String nameOfArgument) {
        this.name = nameOfArgument;

        return this;
    }

    /**
     * Set type of current constructor argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link ConstructorParameterInfo}
     */
    public ConstructorParameterInfo setType(final String typeOfArgument) {
        this.type = typeOfArgument;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link ConstructorInfo}
     */
    public ConstructorInfo next() {
        return this.constructorInfo;
    }
}
