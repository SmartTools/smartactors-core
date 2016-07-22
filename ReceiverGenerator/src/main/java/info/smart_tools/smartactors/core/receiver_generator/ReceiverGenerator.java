package info.smart_tools.smartactors.core.receiver_generator;

import info.smart_tools.smartactors.core.class_generator_java_compile_api.ClassGenerator;
import info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder.ClassBuilder;
import info.smart_tools.smartactors.core.class_generator_java_compile_api.class_builder.Modifiers;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.core.ireceiver_generator.exception.ReceiverGeneratorException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

import java.lang.reflect.Method;

/**
 * Implementation of {@link IReceiverGenerator}.
 * <pre>
 * Main features of implementation:
 * - use class generator based on java compile API;
 * </pre>
 */
public class ReceiverGenerator implements IReceiverGenerator {

    private IClassGenerator<String> classGenerator;

    /**
     * Constructor.
     * Create new instance of {@link ReceiverGenerator} by given {@link ClassLoader}
     * @param classLoader the instance of {@link ClassLoader}
     * @throws InvalidArgumentException if initialization inner instances of {@link FieldName} was failed
     */
    public ReceiverGenerator(final ClassLoader classLoader)
            throws InvalidArgumentException {
        this.classGenerator = new ClassGenerator(classLoader);
    }

    @Override
    public IMessageReceiver generate(
            final Object objInstance,
            final IResolveDependencyStrategy wrapperResolutionStrategy,
            final String methodName
    ) throws InvalidArgumentException, ReceiverGeneratorException {
        if (
                null == objInstance ||
                null == methodName ||
                methodName.isEmpty() ||
                null == wrapperResolutionStrategy
        ) {
            throw new InvalidArgumentException("One of the arguments null or empty.");
        }
        try {
            Class<IMessageReceiver> clazz = generateClass(
                    objInstance,
                    methodName
            );
            return clazz.getConstructor(
                    new Class[]{objInstance.getClass(), IResolveDependencyStrategy.class}
            )
                    .newInstance(new Object[]{objInstance, wrapperResolutionStrategy});
        } catch (Throwable e) {
            throw new ReceiverGeneratorException(
                    "Could not generate message receiver because of the following error:",
                    e
            );
        }
    }

    private Class<IMessageReceiver> generateClass(
            final Object usersObject,
            final String handlerName
    )
            throws Exception {
        ClassBuilder cb = new ClassBuilder("\t", "\n");
        Class wrapperInterface = findWrapperInterface(usersObject, handlerName);

        // Add package name and imports
        cb
                .addPackageName(usersObject.getClass().getPackage().getName())
                .addImport(usersObject.getClass().getCanonicalName())
                .addImport(wrapperInterface.getCanonicalName())
                .addImport(IMessageProcessor.class.getCanonicalName())
                .addImport(IMessageReceiver.class.getCanonicalName())
                .addImport(AsynchronousOperationException.class.getCanonicalName())
                .addImport(MessageReceiveException.class.getCanonicalName())
                .addImport(IResolveDependencyStrategy.class.getCanonicalName())
                .addImport(IObjectWrapper.class.getCanonicalName());

        // Add class header
        cb
                .addClass()
                .setClassModifier(Modifiers.PUBLIC)
                .setClassName(usersObject.getClass().getSimpleName() + "_" + handlerName + "_" + "receiver")
                .setInterfaces(IMessageReceiver.class.getSimpleName());

        // Add fields
        cb
                .addField()
                        .setModifier(Modifiers.PRIVATE)
                        .setType(usersObject.getClass().getSimpleName())
                        .setName("usersObject")
                        .next()
                .addField()
                        .setModifier(Modifiers.PRIVATE)
                        .setType(IResolveDependencyStrategy.class.getSimpleName())
                        .setName("strategy")
                ;

        // Add constructor
        cb
                .addConstructor()
                .setModifier(Modifiers.PUBLIC)
                .setParameters()
                        .setType(usersObject.getClass().getSimpleName())
                        .setName("object")
                        .next()
                .setParameters()
                        .setType(IResolveDependencyStrategy.class.getSimpleName())
                        .setName("strategy")
                        .next()
                .addStringToBody("\tthis.usersObject = object;")
                .addStringToBody("\tthis.strategy = strategy;");

        // Add method
        cb
                .addMethod()
                .setModifier(Modifiers.PUBLIC)
                .addParameter()
                        .setType(IMessageProcessor.class.getSimpleName())
                        .setName("processor")
                        .next()
                .setReturnType("void")
                .setName("receive")
                .setExceptions(MessageReceiveException.class.getSimpleName())
                .setExceptions(AsynchronousOperationException.class.getSimpleName())
                .addStringToBody("try {")
                .addStringToBody("\t" + wrapperInterface.getSimpleName() + " wrapper = this.strategy.resolve();")
                .addStringToBody("\t((IObjectWrapper) wrapper).init(processor.getEnvironment());")
                .addStringToBody("\tthis.usersObject.doSomeWork(wrapper);")
                .addStringToBody("} catch (Throwable e) {")
                .addStringToBody("throw new MessageReceiveException(\"Could not execute receiver operation.\", e);")
                .addStringToBody("}");

        return (Class<IMessageReceiver>) classGenerator.generate(cb.buildClass().toString());
    }

    private Class findWrapperInterface(final Object actor, final String handler) {
        Method[] methods = actor.getClass().getDeclaredMethods();
        Class wrapperInterface = null;
        for (Method m : methods) {
            if (m.getName().equals(handler)) {
                wrapperInterface = m.getParameterTypes()[0];
            }
        }

        return wrapperInterface;
    }
}
