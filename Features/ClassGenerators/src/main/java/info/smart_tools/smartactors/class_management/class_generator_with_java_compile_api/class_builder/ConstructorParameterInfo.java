package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IConstructorInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IConstructorParameterInfo;

/**
 * Parameter summary class
 */
public class ConstructorParameterInfo implements IConstructorParameterInfo {

    private IConstructorInfo constructorInfo;
    private String name;
    private String type;

    /**
     * Constructor.
     * Create new instance of {@link IConstructorParameterInfo} by given {@link IConstructorInfo}
     * @param constructorInfo the link to parent instance of {@link IConstructorInfo}
     */
    public ConstructorParameterInfo(final IConstructorInfo constructorInfo) {
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
     * @return instance of {@link IConstructorParameterInfo}
     */
    public IConstructorParameterInfo setName(final String nameOfArgument) {
        this.name = nameOfArgument;

        return this;
    }

    /**
     * Set type of current constructor argument
     * @param typeOfArgument the type of constructor argument
     * @return instance of {@link IConstructorParameterInfo}
     */
    public IConstructorParameterInfo setType(final String typeOfArgument) {
        this.type = typeOfArgument;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IConstructorInfo}
     */
    public IConstructorInfo next() {
        return this.constructorInfo;
    }
}
