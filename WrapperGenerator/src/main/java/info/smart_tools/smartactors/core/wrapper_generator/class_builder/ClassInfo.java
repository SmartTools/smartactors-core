package info.smart_tools.smartactors.core.wrapper_generator.class_builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class summary info
 */
public class ClassInfo {

    private ClassBuilder builder;
    private String className;
    private Modifiers classModifier;
    private String inherited;
    private List<String> interfaces = new ArrayList<>();

    /**
     * Constructor.
     * Create instance of {@link ClassInfo} by given {@link ClassBuilder}
     * @param builder the link to parent instance of {@link ClassBuilder}
     */
    public ClassInfo(final ClassBuilder builder) {
        this.builder = builder;
    }

    /**
     * Set modifier of class
     * @param modifierOfClass the class modifier
     * @return current instance of {@link ClassInfo}
     */
    public ClassInfo setClassModifier(final Modifiers modifierOfClass) {
        this.classModifier = modifierOfClass;

        return this;
    }

    /**
     * Set name of class
     * @param nameOfClass the name of class
     * @return current instance of {@link ClassInfo}
     */
    public ClassInfo setClassName(final String nameOfClass) {
        this.className = nameOfClass;

        return this;
    }

    /**
     * Set inheritable class
     * @param inheritedClass the name of inherited class
     * @return current instance of {@link ClassInfo}
     */
    public ClassInfo setInherited(final String inheritedClass) {
        this.inherited = inheritedClass;

        return this;
    }

    /**
     * Add new interface by given interface name
     * @param interfaceName the name of given interface
     * @return current instance of {@link ClassInfo}
     */
    public ClassInfo setInterfaces(final String interfaceName) {
        this.interfaces.add(interfaceName);

        return this;
    }

    public String getClassName() {
        return className;
    }

    public Modifiers getClassModifier() {
        return classModifier;
    }

    public String getInherited() {
        return inherited;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    /**
     * Return editing to parent parameters
     * @return the parent instance of {@link ClassBuilder}
     */
    public ClassBuilder next() {
        return this.builder;
    }
}