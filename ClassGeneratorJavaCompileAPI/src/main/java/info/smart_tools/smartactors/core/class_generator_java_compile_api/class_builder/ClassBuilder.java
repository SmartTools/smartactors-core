package info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class builder.
 * Build class by given class attributes.
 */
public class ClassBuilder {

    private String tabSymbol = "\0";
    private String lineBreakSymbol = "\0";
    private List<String> importsList = new ArrayList<>();
    private String packageName = "";
    private ClassInfo classInfo;
    private List<FieldInfo> fields = new ArrayList<>();
    private List<MethodInfo> methods = new ArrayList<>();
    private List<ConstructorInfo> constructors = new ArrayList<>();

    private static final char OPEN = '{';
    private static final char CLOSE = '}';
    private static final char OPEN_B = '(';
    private static final char CLOSE_B = ')';
    private static final char OPEN_TB = '<';
    private static final char CLOSE_TB = '>';
    private static final char SPACE = ' ';
    private static final char COMMA = ',';
    private static final char SEMICOLON = ';';
    private static final String CLASS = "class";
    private static final String EXTENDS = "extends";
    private static final String IMPLEMENTS = "implements";
    private static final String THROWS = "throws";
    private static final String PACKAGE = "package";
    private static final String IMPORT = "import";

    private static Map<String, String> mapPrimitiveToClass = new HashMap<>();
    static {
        mapPrimitiveToClass.put(int.class.getName(), "Integer");
        mapPrimitiveToClass.put(long.class.getName(), "Long");
        mapPrimitiveToClass.put(float.class.getName(), "Float");
        mapPrimitiveToClass.put(double.class.getName(), "Double");
        mapPrimitiveToClass.put(byte.class.getName(), "Byte");
        mapPrimitiveToClass.put(boolean.class.getName(), "Boolean");
        mapPrimitiveToClass.put(char.class.getName(), "Character");
        mapPrimitiveToClass.put(short.class.getName(), "Short");
    }

    /**
     * Constructor.
     * Creates instance of {@link ClassBuilder} with given setting
     * @param tabSymbol the symbol for indent between string beginning and language words
     * @param lineBreakSymbol the newline character
     */
    public ClassBuilder(final String tabSymbol, final String lineBreakSymbol) {
        this.tabSymbol = tabSymbol;
        this.lineBreakSymbol = lineBreakSymbol;
    }

    /**
     * Add package name
     * @param nameOfPackage the name of package
     * @return current instance of {@link ClassBuilder}
     */
    public ClassBuilder addPackageName(final String nameOfPackage) {
        this.packageName = nameOfPackage;

        return this;
    }

    /**
     * Add import
     * @param value the import string
     * @return current instance of {@link ClassBuilder}
     */
    public ClassBuilder addImport(final String value) {
        this.importsList.add(value);

        return this;
    }

    /**
     * Start editing parameters of {@link ClassInfo}
     * @return instance of {@link ClassInfo}
     */
    public ClassInfo addClass() {
        if (null == this.classInfo) {
            this.classInfo = new ClassInfo(this);
        }

        return this.classInfo;
    }

    /**
     * Add new method and start editing parameters of new method
     * @return instance of {@link MethodInfo}
     */
    public MethodInfo addMethod() {
        MethodInfo method = new MethodInfo(this);
        this.methods.add(method);

        return method;
    }

    /**
     * Add new constructor and start editing parameters of new constructor
     * @return instance of {@link ConstructorInfo}
     */
    public ConstructorInfo addConstructor() {
        ConstructorInfo constructorInfo = new ConstructorInfo(this);
        this.constructors.add(constructorInfo);

        return constructorInfo;
    }

    /**
     * Add new field and start editing parameters of new field
     * @return instance of {@link FieldInfo}
     */
    public FieldInfo addField() {
        FieldInfo fieldInfo = new FieldInfo(this);
        this.fields.add(fieldInfo);

        return fieldInfo;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public List<FieldInfo> getFields() {
        return fields;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public List<ConstructorInfo> getConstructors() {
        return constructors;
    }

    /**
     * Build string containing class by completed before class info, fields, constructors and methods
     * @return instance of {@link StringBuilder}
     */
    public StringBuilder buildClass() {
        StringBuilder builder = new StringBuilder();

        // Add package string
        builder
                .append(PACKAGE)
                .append(SPACE)
                .append(this.packageName)
                .append(SEMICOLON)
                .append(this.lineBreakSymbol)
                .append(this.lineBreakSymbol);

        // Add imports
        for (String str : this.importsList) {
            builder
                    .append(IMPORT)
                    .append(SPACE)
                    .append(str)
                    .append(SEMICOLON)
                    .append(this.lineBreakSymbol);
        }
        builder.append(this.lineBreakSymbol);

        // Add class
        builder
                .append(getClassInfo().getClassModifier().getValue())
                .append(SPACE)
                .append(CLASS)
                .append(SPACE)
                .append(getClassInfo().getClassName())
                .append(SPACE);
        if (null != getClassInfo().getInherited() && !getClassInfo().getInherited().isEmpty()) {
            builder
                    .append(EXTENDS)
                    .append(SPACE)
                    .append(getClassInfo().getInherited())
                    .append(SPACE);
        }

        int listSize = getClassInfo().getInterfaces().size();
        int count = 1;
        if (listSize > 0) {
            builder
                    .append(IMPLEMENTS)
                    .append(SPACE);
        }
        for (String str : getClassInfo().getInterfaces()) {
            builder.append(str);
            if (listSize != count) {
                builder.append(COMMA);
            }
            builder.append(SPACE);
            ++count;
        }
        // Open class
        builder
                .append(OPEN)
                .append(this.lineBreakSymbol);

        // Add fields
        for (FieldInfo field : this.getFields()) {
            builder
                    .append(this.tabSymbol)
                    .append(field.getModifier().getValue())
                    .append(SPACE)
                    .append(field.getType());
            if (field.getInnerGenericType() != null && !field.getInnerGenericType().isEmpty()) {
                String genericType = mapPrimitiveToClass.get(field.getInnerGenericType());
                if (null == genericType) {
                    genericType = field.getInnerGenericType();
                }
                builder
                        .append(OPEN_TB)
                        .append(genericType)
                        .append(CLOSE_TB);
            }
            builder
                    .append(SPACE)
                    .append(field.getName())
                    .append(SEMICOLON)
                    .append(this.lineBreakSymbol);
        }
        builder.append(this.lineBreakSymbol);

        // Add constructors
        for (ConstructorInfo constructorInfo : this.getConstructors()) {
            builder
                    .append(this.tabSymbol)
                    .append(constructorInfo.getModifier().getValue())
                    .append(SPACE)
                    .append(getClassInfo().getClassName())
                    .append(OPEN_B);
            listSize = constructorInfo.getParameters().size();
            count = 1;
            for (ConstructorParameterInfo parameterInfo : constructorInfo.getParameters()) {
                builder
                        .append(parameterInfo.getType())
                        .append(SPACE)
                        .append(parameterInfo.getName());
                if (listSize != count) {
                    builder
                            .append(COMMA)
                            .append(SPACE);
                }
                ++count;
            }
            builder
                    .append(CLOSE_B)
                    .append(SPACE);
            listSize = constructorInfo.getExceptions().size();
            count = 1;
            if (listSize > 0) {
                builder
                        .append(THROWS)
                        .append(SPACE);
            }
            for (String exception : constructorInfo.getExceptions()) {
                builder
                        .append(exception);
                if (listSize != count) {
                    builder.append(COMMA);
                }
                builder.append(SPACE);
                ++count;
            }
            builder
                    .append(SPACE)
                    .append(OPEN)
                    .append(this.lineBreakSymbol);
            for (String bodyString : constructorInfo.getBody()) {
                builder
                        .append(this.tabSymbol)
                        .append(this.tabSymbol)
                        .append(bodyString)
                        .append(this.lineBreakSymbol);
            }
            builder
                    .append(this.lineBreakSymbol)
                    .append(CLOSE)
                    .append(this.lineBreakSymbol)
                    .append(this.lineBreakSymbol);
        }

        // Add methods
        for (MethodInfo methodInfo : this.getMethods()) {
            builder
                    .append(this.tabSymbol)
                    .append(methodInfo.getModifier().getValue())
                    .append(SPACE)
                    .append(methodInfo.getReturnType())
                    .append(SPACE)
                    .append(methodInfo.getName())
                    .append(OPEN_B);
            listSize = methodInfo.getParameters().size();
            count = 1;
            for (MethodParameterInfo parameterInfo : methodInfo.getParameters()) {
                builder
                        .append(parameterInfo.getType())
                        .append(SPACE)
                        .append(parameterInfo.getName());
                if (listSize != count) {
                    builder
                            .append(COMMA)
                            .append(SPACE);
                }
                ++count;
            }
            builder
                    .append(CLOSE_B)
                    .append(SPACE);
            listSize = methodInfo.getExceptions().size();
            count = 1;
            if (listSize > 0) {
                builder
                        .append(THROWS)
                        .append(SPACE);
            }
            for (String exception : methodInfo.getExceptions()) {
                builder
                        .append(exception);
                if (listSize != count) {
                    builder.append(COMMA);
                }
                builder.append(SPACE);
                ++count;
            }
            builder
                    .append(SPACE)
                    .append(OPEN)
                    .append(this.lineBreakSymbol);
            for (String bodyString : methodInfo.getBody()) {
                builder
                        .append(this.tabSymbol)
                        .append(this.tabSymbol)
                        .append(bodyString)
                        .append(this.lineBreakSymbol);
            }
            builder
                    .append(this.lineBreakSymbol)
                    .append(CLOSE)
                    .append(this.lineBreakSymbol)
                    .append(this.lineBreakSymbol);
        }

        // Close class
        builder.append(CLOSE);

        return builder;
    }
}




