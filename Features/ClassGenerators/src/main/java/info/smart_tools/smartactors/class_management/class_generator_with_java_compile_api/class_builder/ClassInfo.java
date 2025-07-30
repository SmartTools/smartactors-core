package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder;

import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassInfo;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Class summary info
 */
public class ClassInfo implements IClassInfo {

    private IClassBuilder builder;
    private String className;
    private Modifiers classModifier;
    private String inherited;
    private List<String> interfaces = new ArrayList<>();

    /**
     * Constructor.
     * Create instance of {@link ClassInfo} by given {@link IClassBuilder}
     * @param builder the link to parent instance of {@link IClassBuilder}
     */
    public ClassInfo(final IClassBuilder builder) {
        this.builder = builder;
    }

    /**
     * Set modifier of class
     * @param modifierOfClass the class modifier
     * @return current instance of {@link IClassInfo}
     */
    public IClassInfo setClassModifier(final Modifiers modifierOfClass) {
        this.classModifier = modifierOfClass;

        return this;
    }

    /**
     * Set name of class
     * @param nameOfClass the name of class
     * @return current instance of {@link IClassInfo}
     */
    public IClassInfo setClassName(final String nameOfClass) {
        this.className = nameOfClass;

        return this;
    }

    /**
     * Set inheritable class
     * @param inheritedClass the name of inherited class
     * @return current instance of {@link IClassInfo}
     */
    public IClassInfo setInherited(final String inheritedClass) {
        this.inherited = inheritedClass;

        return this;
    }

    /**
     * Add new interface by given interface name
     * @param interfaceName the name of given interface
     * @return current instance of {@link IClassInfo}
     */
    public IClassInfo setInterfaces(final String interfaceName) {
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
     * @return the parent instance of {@link IClassBuilder}
     */
    public IClassBuilder next() {
        return this.builder;
    }
}