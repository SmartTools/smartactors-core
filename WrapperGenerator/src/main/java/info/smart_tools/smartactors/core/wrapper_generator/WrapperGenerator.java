package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.class_generator_java_compile_api.ClassGenerator;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.core.iwrapper_generator.exception.WrapperGeneratorException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.wrapper_generator.class_builder.ClassBuilder;
import info.smart_tools.smartactors.core.wrapper_generator.class_builder.Modifiers;
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link IWrapperGenerator}.
 * <pre>
 * Main features of implementation:
 * - use class generator based on java compile API;
 * - use supporting class Field;
 * </pre>
 */
public class WrapperGenerator implements IWrapperGenerator {

    private IClassGenerator<String> classGenerator;

    /**
     * Constructor.
     * Create new instance of {@link WrapperGenerator} by given {@link ClassLoader}
     * @param classLoader the instance of {@link ClassLoader}
     */
    public WrapperGenerator(final ClassLoader classLoader) {
        this.classGenerator = new ClassGenerator(classLoader);
    }

    @Override
    public <T> T generate(final Class<T> targetInterface)
            throws InvalidArgumentException, WrapperGeneratorException {
        T instance = null;

        if (null == targetInterface) {
            throw new InvalidArgumentException("Target class should not be null!");
        }
        if (!targetInterface.isInterface()) {
            throw new InvalidArgumentException("Target class should be an interface!");
        }

        try {
            instance = IOC.resolve(Keys.getOrAdd(targetInterface.getCanonicalName()));
        } catch (ResolutionException e) {
            // do nothing
            // ToDo: need refactoring
        }
        if (null != instance) {
            return instance;
        }

        try {
            Class<T> clazz = generateClass(targetInterface);

            // May be later CreateNewInstanceStrategy will be replaced by GetInstanceFromPoolStrategy
            IOC.register(
                    Keys.getOrAdd(targetInterface.getCanonicalName()),
                    new ApplyFunctionToArgumentsStrategy(
                            (arg) ->  {
                                try {
                                    return clazz.newInstance();
                                } catch (Throwable e) {
                                    throw new RuntimeException("Error on creation new instance.", e);
                                }
                            }
                    )
            );

            return IOC.resolve(Keys.getOrAdd(targetInterface.getCanonicalName()));
        } catch (Throwable e) {
            throw new WrapperGeneratorException(
                    "Could not implement wrapper interface because of the following error:",
                    e
            );
        }
    }

    private <T> Class<T> generateClass(final Class<T> targetInterface)
            throws Exception {

        ClassBuilder cb = new ClassBuilder("\t", "\n");

        // Add package name and imports.
        cb
                .addPackageName(targetInterface.getPackage().getName())
                .addImport(IField.class.getCanonicalName())
                .addImport(Field.class.getCanonicalName())
                .addImport(FieldName.class.getCanonicalName())
                .addImport(InvalidArgumentException.class.getCanonicalName())
                .addImport(targetInterface.getCanonicalName())
                .addImport(IObject.class.getCanonicalName())
                .addImport(IObjectWrapper.class.getCanonicalName())
                .addImport(ReadValueException.class.getCanonicalName())
                .addImport(ChangeValueException.class.getCanonicalName())
                .addImport(IFieldName.class.getCanonicalName());

        Map<Class<?>, String> types = new HashMap<>();
        for (Method m : targetInterface.getMethods()) {
            Class<?> returnType = m.getReturnType();
            if (!returnType.isPrimitive()) {
                types.put(returnType, returnType.getCanonicalName());
            }
            Class<?>[] args = m.getParameterTypes();
            for (Class<?> c : args) {
                if (!c.isPrimitive()) {
                    types.put(c, c.getCanonicalName());
                }
            }
        }
        for (Object o : types.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            cb.addImport((String) entry.getValue());
        }

        // Add class header
        cb
                .addClass()
                        .setClassModifier(Modifiers.PUBLIC)
                        .setClassName(targetInterface.getSimpleName() + "Impl")
                        .setInterfaces("IObjectWrapper")
                        .setInterfaces(targetInterface.getSimpleName());

        // Add class fields
        for (Method m : targetInterface.getMethods()) {
            String methodPrefix;
            if (Void.TYPE == m.getGenericReturnType()) {
                methodPrefix = "out";
            } else {
                methodPrefix = "in";
            }
            cb
                    .addField().setModifier(Modifiers.PRIVATE).setType("IField")
                            .setName("fieldFor_" + methodPrefix + "_" + m.getName());
        }

        // Add class constructors
        StringBuilder builder = new StringBuilder();
        builder.append("\t\t").append("try {\n");
        for (Method m : targetInterface.getMethods()) {
            String methodPrefix = Void.TYPE == m.getGenericReturnType() ? "out" : "in";
            builder
                    .append("\t\t\t").append("this.fieldFor_")
                    .append(methodPrefix)
                    .append("_")
                    .append(m.getName())
                    .append(" = new ")
                    .append("Field(new FieldName(\"")
                    .append(methodPrefix).append("_").append(m.getName()).append("\"" + "));\n");
        }
        builder
                .append("\t\t").append("} catch (Exception e) {\n").append("\t\t\t")
                .append("throw new InvalidArgumentException(\"\", e);\n")
                .append("\t\t").append("}");
        cb
                .addConstructor().setModifier(Modifiers.PUBLIC).setExceptions("InvalidArgumentException")
                        .addStringToBody(builder.toString());

        // Add 'IObject[] args;' field
        cb
                .addField().setModifier(Modifiers.PRIVATE).setType("IObject").setName("env");
        // Add init method
        cb
                .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("void").setName("init")
                .addParameter()
                .setType("IObject").setName("environments").next()
                .addStringToBody("this.env = environments;");

        // Add 'getIObjects' method
        cb
                .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("IObject").setName("getEnvironmentIObject")
                .addParameter()
                        .setType("IFieldName")
                        .setName("fieldName")
                        .next()
                .setExceptions("InvalidArgumentException")
                .addStringToBody("try {")
                .addStringToBody("\treturn (IObject) this.env.getValue(fieldName);")
                .addStringToBody("} catch (Throwable e) {")
                .addStringToBody("throw new InvalidArgumentException(\"Could not get IObject from environments.\", e);")
                .addStringToBody("}");

        // Add getters and setters
        for (Method m : targetInterface.getMethods()) {
            String methodPrefix = Void.TYPE == m.getGenericReturnType() ? "out" : "in";
            if (!m.getReturnType().equals(Void.TYPE)) {
                cb
                        .addMethod().setModifier(Modifiers.PUBLIC).setReturnType(m.getGenericReturnType().getTypeName())
                                .setName(m.getName()).setExceptions("ReadValueException")
                        .addStringToBody("try {")
                        .addStringToBody(
                                "\treturn fieldFor_" +
                                methodPrefix +
                                "_" +
                                m.getName() +
                                ".in(this.env);"
                        )
                        .addStringToBody("} catch(Throwable e) {")
                        .addStringToBody("\tthrow new ReadValueException(\"Could not get value from iobject.\", e);")
                        .addStringToBody("}");
            } else {
                cb
                        .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("void").setName(m.getName())
                        .setExceptions("ChangeValueException")
                        .addParameter()
                                .setType(m.getGenericParameterTypes()[0].getTypeName()).setName("value").next()
                        .addStringToBody("try {")
                        .addStringToBody(
                                "\tthis.fieldFor_" +
                                methodPrefix +
                                "_" +
                                m.getName() +
                                ".out(this.env, value);"
                        )
                        .addStringToBody("} catch (Throwable e) {")
                        .addStringToBody("\tthrow new ChangeValueException(\"Could not set value from iobject.\", e);")
                        .addStringToBody("}");
            }
        }

        return (Class<T>) classGenerator.generate(cb.buildClass().toString());
    }
}


