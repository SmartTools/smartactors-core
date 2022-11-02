package info.smart_tools.smartactors.class_management.interfaces.class_builder;

import java.util.List;

public interface IConstructorInfo {

    Modifiers getModifier();

    /**
     * Set modifier of constructor
     * @param modifierOfClass the constructor modifier
     * @return current instance of {@link IConstructorInfo}
     */
    IConstructorInfo setModifier(final Modifiers modifierOfClass);

    List<IConstructorParameterInfo> getParameters();

    /**
     * Add new parameter to the current constructor and start editing arguments of new parameter
     * @return instance of {@link IConstructorParameterInfo}
     */
    IConstructorParameterInfo setParameters();

    List<String> getExceptions();

    /**
     * Add new exception to the current constructor by given exception name
     * @param exception the name of given exception
     * @return current instance of {@link IConstructorInfo}
     */
    IConstructorInfo setExceptions(final String exception);

    List<String> getBody();

    /**
     * Add next string to the constructor body
     * @param string the string with constructor code
     * @return current instance of {@link IConstructorInfo}
     */
    IConstructorInfo addStringToBody(final String string);

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    IClassBuilder next();
}
