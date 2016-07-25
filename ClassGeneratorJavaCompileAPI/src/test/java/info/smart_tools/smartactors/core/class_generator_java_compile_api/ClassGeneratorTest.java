package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import info.smart_tools.smartactors.core.iclass_generator.exception.ClassGenerationException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests for {@link ClassGenerator}
 */
public class ClassGeneratorTest {

    @Test
    public void check()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        String testSample = "package info.smart_tools.smartactors.core.test_class;\n" +
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class<?> clazz = classGenerator.generate(testSample);
        TestInterface inst = (TestInterface) clazz.newInstance();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullArg()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        classGenerator.generate(null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnEmptyArg()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        classGenerator.generate("");
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTestArgumentWithoutPackage()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        String testSample =
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        classGenerator.generate(testSample);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnTestArgumentWithoutClass()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        String testSample = "package info.smart_tools.smartactors.core.test_class;\n" +
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "public TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        classGenerator.generate(testSample);
        fail();
    }

    @Test (expected = ClassGenerationException.class)
    public void checkInvalidArgumentExceptionOnTestWithUndefinedInterface()
            throws Exception {
        ClassGenerator classGenerator = new ClassGenerator(null);
        String testSample = "package info.smart_tools.smartactors.core.test_class;\n" +
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterfaceUndefined {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        classGenerator.generate(testSample);
        fail();
    }

    @Test
    public void checkClassGenerationWithUsingJarFile()
            throws Exception {

        String pathToJar = this.getClass().getClassLoader().getResource("ifield_name.jar").getFile();
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

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

        ClassGenerator classGenerator = new ClassGenerator(cl);
        String testSample = "package info.smart_tools.smartactors.core.test_class;\n" +
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "import info.smart_tools.smartactors.core.ifield_name.IFieldName;\n" +
                "public class TestClass implements TestInterface, IFieldName {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class newClass = classGenerator.generate(testSample);
        assertNotNull(newClass);
    }
}

