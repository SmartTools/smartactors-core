package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IFieldInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;

/**
 * Field summary info
 */
public class FieldInfo implements IFieldInfo {

    private IClassBuilder classBuilder;

    private Modifiers modifier;
    private String name;
    private String type;
    private String innerGenericType;

    /**
     * Constructor.
     * Create new instance of {@link IFieldInfo} by given {@link IClassBuilder}
     * @param classBuilder the link to parent instance of {@link IClassBuilder}
     */
    public FieldInfo(final IClassBuilder classBuilder) {
        this.classBuilder = classBuilder;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    /**
     * Set modifier of field
     * @param modifierOfField the constructor modifier
     * @return instance of {@link IFieldInfo}
     */
    public IFieldInfo setModifier(final Modifiers modifierOfField) {
        this.modifier = modifierOfField;

        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Set name of current field
     * @param nameOfField the name of field
     * @return instance of {@link IFieldInfo}
     */
    public IFieldInfo setName(final String nameOfField) {
        this.name = nameOfField;

        return this;
    }

    public String getType() {
        return type;
    }

    /**
     * Set type of current field
     * @param typeOfField the type of field
     * @return instance of {@link IFieldInfo}
     */
    public IFieldInfo setType(final String typeOfField) {
        this.type = typeOfField;

        return this;
    }

    public String getInnerGenericType() {
        return innerGenericType;
    }

    /**
     * Set generic type of current field
     * @param genericType the generic type of field
     * @return instance of {@link IFieldInfo}
     */
    public IFieldInfo setInnerGenericType(final String genericType) {
        this.innerGenericType = genericType;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    public IClassBuilder next() {
        return this.classBuilder;
    }
}
