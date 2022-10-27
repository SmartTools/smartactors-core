package info.smart_tools.smartactors.class_management.interfaces.class_builder;

import java.util.List;

public interface IClassBuilder {

    /**
     * Add package name
     * @param nameOfPackage the name of package
     * @return current instance of {@link IClassBuilder}
     */
    IClassBuilder addPackageName(final String nameOfPackage);

    /**
     * Add import
     * @param value the import string
     * @return current instance of {@link IClassBuilder}
     */
    IClassBuilder addImport(final String value);

    /**
     * Start editing parameters of {@link IClassInfo}
     * @return instance of {@link IClassInfo}
     */
    IClassInfo addClass();

    /**
     * Add new method and start editing parameters of new method
     * @return instance of {@link IMethodInfo}
     */
    IMethodInfo addMethod();

    /**
     * Add new constructor and start editing parameters of new constructor
     * @return instance of {@link IConstructorInfo}
     */
    IConstructorInfo addConstructor();

    /**
     * Add new field and start editing parameters of new field
     * @return instance of {@link IFieldInfo}
     */
    IFieldInfo addField();

    IClassInfo getClassInfo();

    List<IFieldInfo> getFields();

    List<IMethodInfo> getMethods();

    List<IConstructorInfo> getConstructors();

    /**
     * Build string containing class by completed before class info, fields, constructors and methods
     * @return instance of {@link StringBuilder}
     */
    StringBuilder buildClass();
}
