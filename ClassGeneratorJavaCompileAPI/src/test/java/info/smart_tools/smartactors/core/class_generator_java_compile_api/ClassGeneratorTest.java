package info.smart_tools.smartactors.core.class_generator_java_compile_api;

import org.junit.Test;

/**
 * Tests for {@link ClassGenerator}
 */
public class ClassGeneratorTest {

    @Test
    public void check()
            throws Exception {
        ClassGenerator cl = new ClassGenerator(null);
        String testSample = "package info.smart_tools.smartactors.core.test_class;\n" +
                "import info.smart_tools.smartactors.core.class_generator_java_compile_api.TestInterface;\n" +
                "public class TestClass implements TestInterface {\n" +
                "    private int a;\n" +
                "    public Integer getA() {\n" +
                "        return a;\n" +
                "    }\n" +
                "}\n";
        Class<?> clazz = cl.generate(testSample);
        TestInterface inst = (TestInterface) clazz.newInstance();
    }
}

