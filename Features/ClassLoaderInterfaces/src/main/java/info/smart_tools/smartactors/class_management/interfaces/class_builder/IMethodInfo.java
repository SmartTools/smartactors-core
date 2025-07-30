package info.smart_tools.smartactors.class_management.interfaces.class_builder;

import java.util.List;

public interface IMethodInfo {

    Modifiers getModifier();

    /**
     * Set modifier of method
     * @param modifierOfMethod the method modifier
     * @return instance of {@link IMethodInfo}
     */
    IMethodInfo setModifier(final Modifiers modifierOfMethod);

    String getName();

    /**
     * Set name of current method
     * @param nameOfMethod the name of method
     * @return instance of {@link IMethodInfo}
     */
    IMethodInfo setName(final String nameOfMethod);

    List<IMethodParameterInfo> getParameters();

    /**
     * Add new parameter to the current method and start editing arguments of new parameter
     * @return instance of {@link IConstructorParameterInfo}
     */
    IMethodParameterInfo addParameter();

    List<String> getExceptions();

    /**
     * Add new exception to the current method by given exception name
     * @param exception the name of given exception
     * @return current instance of {@link IMethodInfo}
     */
    IMethodInfo setExceptions(final String exception);

    List<String> getBody();

    /**
     * Add next string to the method body
     * @param string the string with constructor code
     * @return current instance of {@link IMethodInfo}
     */
    IMethodInfo addStringToBody(final String string);

    String getReturnType();

    /**
     * Set type of method return value
     * @param type the type of returning value
     * @return current instance of {@link IMethodInfo}
     */
    IMethodInfo setReturnType(final String type);

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    IClassBuilder next();
}
