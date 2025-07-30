package info.smart_tools.smartactors.class_management.interfaces.class_builder;

public interface IFieldInfo {

    Modifiers getModifier();

    /**
     * Set modifier of field
     * @param modifierOfField the constructor modifier
     * @return instance of {@link IFieldInfo}
     */
    IFieldInfo setModifier(final Modifiers modifierOfField);

    String getName();

    /**
     * Set name of current field
     * @param nameOfField the name of field
     * @return instance of {@link IFieldInfo}
     */
    IFieldInfo setName(final String nameOfField);

    String getType();

    /**
     * Set type of current field
     * @param typeOfField the type of field
     * @return instance of {@link IFieldInfo}
     */
    IFieldInfo setType(final String typeOfField);

    String getInnerGenericType();

    /**
     * Set generic type of current field
     * @param genericType the generic type of field
     * @return instance of {@link IFieldInfo}
     */
    IFieldInfo setInnerGenericType(final String genericType);

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    IClassBuilder next();
}
