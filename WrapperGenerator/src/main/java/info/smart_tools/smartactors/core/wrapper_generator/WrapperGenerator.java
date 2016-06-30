package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.class_generator_java_compile_api.ClassGenerator;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.FieldName;
import info.smart_tools.smartactors.core.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.core.iwrapper_generator.exception.WrapperGeneratorException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.wrapper_generator.class_builder.ClassBuilder;
import info.smart_tools.smartactors.core.wrapper_generator.class_builder.Modifiers;

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
    public <T> T generate(final Class<T> targetInterface, final IObject binding)
            throws InvalidArgumentException, WrapperGeneratorException {
        T instance = null;

        if (null == targetInterface) {
            throw new InvalidArgumentException("Target class should not be null!");
        }
        if (!targetInterface.isInterface()) {
            throw new InvalidArgumentException("Target class should be an interface!");
        }
        if (null == binding) {
            throw new InvalidArgumentException("Binding object should be null!");
        }

        try {
            instance = IOC.resolve(Keys.getOrAdd(targetInterface.toString()));
        } catch (ResolutionException e) {
            // do nothing
        }
        if (null != instance) {
            return instance;
        }

        try {
            Class<T> clazz = generateClass(targetInterface, binding);

            // May be later CreateNewInstanceStrategy will be replaced by GetInstanceFromPoolStrategy
            IOC.register(
                    Keys.getOrAdd(targetInterface.toString()),
                    new CreateNewInstanceStrategy(
                            (arg) ->  {
                                try {
                                    return clazz.newInstance();
                                } catch (Throwable e) {
                                    throw new RuntimeException("Error on creation new instance.", e);
                                }
                            }
                    )
            );

            return IOC.resolve(Keys.getOrAdd(targetInterface.toString()));
        } catch (Throwable e) {
            throw new WrapperGeneratorException(
                    "Could not implement wrapper interface because of the following error:",
                    e
            );
        }
    }

    private <T> Class<T> generateClass(final Class<T> targetInterface, final IObject binding)
            throws Exception {
        IObject currentBinding = (IObject) binding.getValue(new FieldName(targetInterface.toString()));

        ClassBuilder cb = new ClassBuilder("\t", "\n");

        // Add package name and imports.
        cb
                .addPackageName(targetInterface.getPackage().getName())
                .addImport(Field.class.getCanonicalName())
                .addImport(FieldName.class.getCanonicalName())
                .addImport(InvalidArgumentException.class.getCanonicalName())
                .addImport(targetInterface.getCanonicalName())
                .addImport(IObject.class.getCanonicalName())
                .addImport(IObjectWrapper.class.getCanonicalName())
                .addImport(ReadValueException.class.getCanonicalName())
                .addImport(ChangeValueException.class.getCanonicalName());

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
                .addClass().setClassModifier(Modifiers.PUBLIC).setClassName(targetInterface.getSimpleName() + "Impl")
                .setInterfaces("IObjectWrapper").setInterfaces(targetInterface.getSimpleName());

        // Add class fields
        for (Method m : targetInterface.getMethods()) {
            String genericType =
                    Void.TYPE == m.getGenericReturnType() ?
                            m.getGenericParameterTypes()[0].getTypeName() :
                            m.getReturnType().getTypeName();
            cb
                    .addField().setModifier(Modifiers.PRIVATE).setType("Field").setInnerGenericType(genericType)
                            .setName("fieldFor_" + m.getName());
        }

        // Add class constructors
        StringBuilder builder = new StringBuilder();
        builder.append("\t\t").append("try {\n");
        for (Method m : targetInterface.getMethods()) {
            IObject methodBinding = (IObject) currentBinding.getValue(new FieldName(m.getName()));
            builder
                    .append("\t\t\t").append("this.fieldFor_").append(m.getName())
                    .append(" = new Field<>(new FieldName(\"")
                    .append((String) methodBinding.getValue(new FieldName("ValueName")))
                    .append("\")").append(");\n");
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
                .addField().setModifier(Modifiers.PRIVATE).setType("IObject[]").setName("args");
        // Add init method
        cb
                .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("void").setName("init")
                .addParameter()
                .setType("IObject[]").setName("args").next()
                .addStringToBody("this.args = args;");
        // Add 'getIObjects' method
        cb
                .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("IObject[]").setName("getIObjects")
                .addStringToBody("return this.args;");

        // Add getters and setters
        String[] args = (String[]) currentBinding.getValue(new FieldName("initMethodParameters"));
        Map<String, Integer> argumentMap = new HashMap<>();
        int index = 0;
        for (String argName : args) {
            argumentMap.put(argName, index);
            ++index;
        }
        for (Method m : targetInterface.getMethods()) {
            IObject methodBinding = (IObject) currentBinding.getValue(new FieldName(m.getName()));
            String methodType = (String) methodBinding.getValue(new FieldName("MethodType"));
            String resource = (String) methodBinding.getValue(new FieldName("Resource"));
            String strategy = (String) methodBinding.getValue(new FieldName("UseStrategy"));
            String typeTo = m.getReturnType().getSimpleName();

            boolean checkOrGenerate = (boolean) methodBinding.getValue(new FieldName("CheckWrapper"));
            if (null != methodType && methodType.equals("get")) {
                if (m.getExceptionTypes().length != 1 || m.getExceptionTypes()[0] != ReadValueException.class) {
                    throw new Exception("Given interface doesn't satisfy library convention.");
                }
                if (checkOrGenerate) {
                    this.generate(m.getReturnType(), binding);
                }
                cb
                        .addMethod().setModifier(Modifiers.PUBLIC).setReturnType(m.getGenericReturnType().getTypeName())
                                .setName(m.getName()).setExceptions("ReadValueException")
                        .addStringToBody("try {")
                        .addStringToBody(
                                "\treturn fieldFor_" +
                                m.getName() +
                                ".from(" + "this.args[" +
                                argumentMap.get(resource) +
                                "]" + ", " +
                                (null == strategy || strategy.isEmpty() ? typeTo + ".class" : "\"" + strategy + "\"") +
                                ");"
                        )
                        .addStringToBody("} catch(Throwable e) {")
                        .addStringToBody("\tthrow new ReadValueException(\"Could not get value from iobject.\", e);")
                        .addStringToBody("}");
            }
            if (null != methodType && methodType.equals("set")) {
                if (m.getExceptionTypes().length != 1 || m.getExceptionTypes()[0] != ChangeValueException.class) {
                    throw new Exception("Given interface doesn't satisfy library convention.");
                }
                if (checkOrGenerate) {
                    this.generate(m.getParameterTypes()[0], binding);
                }
                cb
                        .addMethod().setModifier(Modifiers.PUBLIC).setReturnType("void").setName(m.getName())
                        .setExceptions("ChangeValueException")
                        .addParameter()
                                .setType(m.getGenericParameterTypes()[0].getTypeName()).setName("value").next()
                        .addStringToBody("try {")
                        .addStringToBody(
                                "\tthis.fieldFor_" +
                                m.getName() +
                                ".inject(" + "this.args[" + argumentMap.get(resource) + "]" + ", value);"
                        )
                        .addStringToBody("} catch (Throwable e) {")
                        .addStringToBody("\tthrow new ChangeValueException(\"Could not set value from iobject.\", e);")
                        .addStringToBody("}");
            }
        }

        return (Class<T>) classGenerator.generate(cb.buildClass().toString());
    }
}


