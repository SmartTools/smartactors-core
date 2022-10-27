package info.smart_tools.smartactors.message_processing.receiver_generator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.FromStringClassGenerator;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder.ClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.exception.ReceiverGeneratorException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ReceiverGenerator}
 */
public class ReceiverGeneratorTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void init() throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        IOC.register(
                Keys.getKeyByName("class-generator:from-string"),
                new ApplyFunctionToArgumentsStrategy((args) -> new FromStringClassGenerator()
                ));
        IOC.register(
                Keys.getKeyByName("class-builder:from_string"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new ClassBuilder((String) args[0], (String) args[1])
                )
        );
    }

    private void attachJarToClassLoader(final String jarName, final ISmartactorsClassLoader cl)
            throws Exception {
        String pathToJar = this.getClass().getClassLoader().getResource(jarName).getFile();
        cl.addURL(new URL("jar:file:" + pathToJar+"!/"));
    }

    @Test
    public void checkCreation()
            throws Exception {
        ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
        attachJarToClassLoader("CustomActorR.jar", cl);
        Class clazz = cl.loadClass("info.smart_tools.smartactors.message_processing.receiver_generator.CustomActorR");
        Object a = clazz.newInstance();
        //CustomActor a = new CustomActor();
        CustomWrapper w = new CustomWrapper();
        w.setGetterUsed(false);
        w.setSetterUsed(false);
        IStrategy returnCustomActorStrategy = mock(IStrategy.class);
        IStrategy returnWrapperStrategy = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("actorID"), returnCustomActorStrategy);
        IOC.register(Keys.getKeyByName(ICustomWrapper.class.getCanonicalName()), returnWrapperStrategy);
        when(returnCustomActorStrategy.resolve()).thenReturn(a);
        when(returnWrapperStrategy.resolve()).thenReturn(w);
        IObject configs = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject wrapperConfig = mock(IObject.class);
        when(env.getValue(new FieldName("int"))).thenReturn(1);
        doNothing().when(env).setValue(new FieldName("int"), 2);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(processor.getEnvironment()).thenReturn(w);
        IReceiverGenerator rg = new ReceiverGenerator();
        assertNotNull(rg);
        IStrategy strategy = mock(IStrategy.class);
        when(strategy.resolve()).thenReturn(w);
        IMessageReceiver r = rg.generate(a, strategy, "doSomeWork");
        assertNotNull(r);
        r.receive(processor);
        assertTrue(w.getGetterUsed());
        assertTrue(w.getSetterUsed());
    }

    @Test(expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullParameter()
            throws Exception {
        IReceiverGenerator rg = new ReceiverGenerator();
        rg.generate(null, null, null);
        fail();
    }

    @Test(expected = ReceiverGeneratorException.class)
    public void checkReceiverGeneratorExceptionOn()
            throws Exception {
        CustomActor a = new CustomActor();
        IStrategy strategy = mock(IStrategy.class);

        IReceiverGenerator rg = new ReceiverGenerator();
        rg.generate(a, strategy, "a");
        fail();
    }
}
