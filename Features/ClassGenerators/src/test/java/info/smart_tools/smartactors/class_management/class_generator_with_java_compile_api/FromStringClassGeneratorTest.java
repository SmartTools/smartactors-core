package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.iclass_generator.exception.ClassGenerationException;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import org.junit.Test;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests for {@link FromStringClassGenerator}
 */
public class FromStringClassGeneratorTest {

    @Test
    public void check()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        String testSample = "package info.smart_tools.smartactors.class_management.test_class;\n" +
                "import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class<?> clazz = classGenerator.generate(testSample, (ClassLoader) ModuleManager.getCurrentClassLoader());
        TestInterface inst = (TestInterface) clazz.newInstance();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullArg()
            throws Exception {
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        classGenerator.generate(null, this.getClass().getClassLoader());
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnEmptyArg()
            throws Exception {
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        classGenerator.generate("", this.getClass().getClassLoader());
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTestArgumentWithoutPackage()
            throws Exception {
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        String testSample =
                "import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        classGenerator.generate(testSample, this.getClass().getClassLoader());
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTestArgumentWithoutClass()
            throws Exception {
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        String testSample = "package info.smart_tools.smartactors.class_management.test_class;\n" +
                "import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_apiTestInterface;\n" +
                "public TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        classGenerator.generate(testSample, this.getClass().getClassLoader());
        fail();
    }

    @Test (expected = ClassGenerationException.class)
    public void checkInvalidArgumentExceptionOnTestWithUndefinedInterface()
            throws Exception {
        ModuleManager.addModule("cl", "cl", "cl");
        ModuleManager.setCurrentModule(ModuleManager.getModuleById("cl"));
        ClassLoader cl = (ClassLoader) ModuleManager.getCurrentClassLoader();
        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        String testSample = "package info.smart_tools.smartactors.class_management.test_class;\n" +
                "import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterfaceUndefined {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class clazz = classGenerator.generate(testSample, cl);
        fail();
    }

    @Test
    public void checkClassGenerationWithUsingJarFile()
            throws Exception {

        String pathToJar = this.getClass().getClassLoader().getResource("ifield_name.jar").getFile();
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL url = new URL("jar:file:" + pathToJar+"!/");
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
        cl.addURL(url);
        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);

        }

        FromStringClassGenerator classGenerator = new FromStringClassGenerator();
        String testSample = "package info.smart_tools.smartactors.class_management.test_class;\n" +
                "import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.TestInterface;\n" +
                "import info.smart_tools.smartactors.core.ifield_name.IFieldName;\n" +
                "public class TestClass implements TestInterface, IFieldName {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class newClass = classGenerator.generate(testSample, (ClassLoader)cl);
        assertNotNull(newClass);
    }
}

