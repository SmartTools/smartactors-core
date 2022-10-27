package info.smart_tools.smartactors.message_processing.receiver_generator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.IClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;
import info.smart_tools.smartactors.class_management.interfaces.iclass_generator.IClassGenerator;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.exception.ReceiverGeneratorException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

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
     * @throws Exception if any error occured
     */
    public ReceiverGenerator()
            throws Exception {
        this.classGenerator = IOC.resolve(Keys.getKeyByName("class-generator:from-string"));;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IMessageReceiver generate(
            final Object objInstance,
            final IStrategy wrapperResolutionStrategy,
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
            Class objClass = objInstance.getClass();
            Class<IMessageReceiver> clazz = (Class<IMessageReceiver>) objClass.getClassLoader().loadClass(
                    objClass.getName()+"_" + methodName + "_" + "receiver"
            );
            return clazz.newInstance();

        } catch (Throwable e) {
            // do nothing in case if receiver implementation class failed to be loaded
            // or new instance of receiver implementation failed to be created
        }

        try {
            Class<IMessageReceiver> clazz = generateClass(
                    objInstance,
                    methodName
            );
            return clazz.getConstructor(
                    new Class[]{objInstance.getClass(), IStrategy.class}
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
        IClassBuilder cb = IOC.resolve(
                Keys.getKeyByName("class-builder:from_string"),
                "\t", "\n"
        );
        Class wrapperInterface = findWrapperInterface(usersObject, handlerName);

        // Add package name and imports
        cb
                .addPackageName(usersObject.getClass().getPackage().getName())
                .addImport(usersObject.getClass().getCanonicalName())
                .addImport(wrapperInterface.getCanonicalName())
                .addImport("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor")
                .addImport(IMessageReceiver.class.getCanonicalName())
                .addImport(AsynchronousOperationException.class.getCanonicalName())
                .addImport(MessageReceiveException.class.getCanonicalName())
                .addImport(IStrategy.class.getCanonicalName())
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
                        .setType(IStrategy.class.getSimpleName())
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
                        .setType(IStrategy.class.getSimpleName())
                        .setName("strategy")
                        .next()
                .addStringToBody("\tthis.usersObject = object;")
                .addStringToBody("\tthis.strategy = strategy;");

        // Add method receive
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
                .addStringToBody("\tthis.usersObject." + handlerName + "(wrapper);")
                .addStringToBody("} catch (Throwable e) {")
                .addStringToBody("throw new MessageReceiveException(\"Could not execute receiver operation.\", e);")
                .addStringToBody("}");

        // Add method dispose
        cb
                .addMethod()
                .setModifier(Modifiers.PUBLIC)
                .setReturnType("void")
                .setName("dispose");

        return (Class<IMessageReceiver>) classGenerator.generate(cb.buildClass().toString(), usersObject.getClass().getClassLoader());
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
