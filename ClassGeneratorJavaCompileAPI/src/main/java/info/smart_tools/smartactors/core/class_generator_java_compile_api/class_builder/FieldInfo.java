package info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder;

/**
 * Field summary info
 */
public class FieldInfo {

    private ClassBuilder classBuilder;

    private Modifiers modifier;
    private String name;
    private String type;
    private String innerGenericType;

    /**
     * Constructor.
     * Create new instance of {@link FieldInfo} by given {@link ClassBuilder}
     * @param classBuilder the link to parent instance of {@link ClassBuilder}
     */
    public FieldInfo(final ClassBuilder classBuilder) {
        this.classBuilder = classBuilder;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    /**
     * Set modifier of field
     * @param modifierOfField the constructor modifier
     * @return instance of {@link FieldInfo}
     */
    public FieldInfo setModifier(final Modifiers modifierOfField) {
        this.modifier = modifierOfField;

        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Set name of current field
     * @param nameOfField the name of field
     * @return instance of {@link FieldInfo}
     */
    public FieldInfo setName(final String nameOfField) {
        this.name = nameOfField;

        return this;
    }

    public String getType() {
        return type;
    }

    /**
     * Set type of current field
     * @param typeOfField the type of field
     * @return instance of {@link FieldInfo}
     */
    public FieldInfo setType(final String typeOfField) {
        this.type = typeOfField;

        return this;
    }

    public String getInnerGenericType() {
        return innerGenericType;
    }

    /**
     * Set generic type of current field
     * @param genericType the generic type of field
     * @return instance of {@link FieldInfo}
     */
    public FieldInfo setInnerGenericType(final String genericType) {
        this.innerGenericType = genericType;

        return this;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link ClassBuilder}
     */
    public ClassBuilder next() {
        return this.classBuilder;
    }
}
