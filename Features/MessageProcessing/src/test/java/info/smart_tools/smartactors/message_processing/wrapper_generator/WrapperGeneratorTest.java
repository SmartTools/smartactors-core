package info.smart_tools.smartactors.message_processing.wrapper_generator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.FromStringClassGenerator;
import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder.ClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.exception.WrapperGeneratorException;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * Tests for {@link WrapperGenerator}
 */
public class WrapperGeneratorTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Before
    public void init()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        IKey fieldKey = Keys.getKeyByName(IField.class.getCanonicalName());
        IOC.register(
                fieldKey,
                new ResolveByNameIocStrategy(
                        (args) -> {
                            String fieldName = String.valueOf(args[0]);
                            try {
                                return new Field(
                                        IOC.resolve(
                                                Keys.getKeyByName(IFieldName.class.getCanonicalName()),
                                                fieldName
                                        )
                                );
                            } catch (InvalidArgumentException | ResolutionException e) {
                                throw new RuntimeException("Can't resolve IField: ", e);
                            }
                        }
                )
        );
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

    private void attachJarToClassLoader(String jarName, ISmartactorsClassLoader cl)
            throws Exception {
        String pathToJar = this.getClass().getClassLoader().getResource(jarName).getFile();
        cl.addURL(new URL("jar:file:" + pathToJar+"!/"));
    }

    @Test
    public void checkCreationAndUsageWrapperByInterface()
            throws Exception {
        ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
        IWrapperGenerator wg = new WrapperGenerator();
        IWrapper w1 = wg.generate(IWrapper.class);
        assertNotNull(w1);
        // re-usage
        IWrapper w2 = wg.generate(IWrapper.class);
        assertNotNull(w2);
        assertNotSame(w1, w2);

        attachJarToClassLoader("ISampleWrapper.jar", cl);
        Class iSampleWrapperClass = cl.loadClass("info.smart_tools.smartactors.message_processing.wrapper_generator.ISampleWrapper");

        Object w3 = wg.generate(iSampleWrapperClass);
        assertNotNull(w3);
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutReadValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator();
        wg.generate(IIncorrectWrapperWithoutReadValueException.class);
    }

    @Test (expected = WrapperGeneratorException.class)
    public void checkOnIncorrectInterfaceWithoutChangeValueException()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator();
        wg.generate(IIncorrectWrapperWithoutChangeValueException.class);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTargetInterfaceNull()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator();
        wg.generate(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNotInterface()
            throws Exception {
        IWrapperGenerator wg = new WrapperGenerator();
        wg.generate(TestClass.class);
        fail();
    }

    @Test
    public void checkFastReturnOnSecondGenerationSameInterface()
            throws Exception {
    }
}

