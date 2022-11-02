package info.smart_tools.smartactors.class_management.interfaces.class_builder;

import java.util.List;

public interface IClassInfo {

    /**
     * Set modifier of class
     * @param modifierOfClass the class modifier
     * @return current instance of {@link IClassInfo}
     */
    IClassInfo setClassModifier(final Modifiers modifierOfClass);

    /**
     * Set name of class
     * @param nameOfClass the name of class
     * @return current instance of {@link IClassInfo}
     */
    IClassInfo setClassName(final String nameOfClass);

    /**
     * Set inheritable class
     * @param inheritedClass the name of inherited class
     * @return current instance of {@link IClassInfo}
     */
    IClassInfo setInherited(final String inheritedClass);

    /**
     * Add new interface by given interface name
     * @param interfaceName the name of given interface
     * @return current instance of {@link IClassInfo}
     */
    IClassInfo setInterfaces(final String interfaceName);

    String getClassName();

    Modifiers getClassModifier();

    String getInherited();

    List<String> getInterfaces();

    /**
     * Return for editing parent parameters
     * @return the parent instance of {@link IClassBuilder}
     */
    IClassBuilder next();
}
