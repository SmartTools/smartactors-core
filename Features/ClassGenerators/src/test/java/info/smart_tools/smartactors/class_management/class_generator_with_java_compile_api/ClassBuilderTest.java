package info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api;

import info.smart_tools.smartactors.class_management.class_generator_with_java_compile_api.class_builder.ClassBuilder;
import info.smart_tools.smartactors.class_management.interfaces.class_builder.Modifiers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for class builder
 */
public class ClassBuilderTest {

    @Test
    public void checkClassBuilder() {
        ClassBuilder cb = new ClassBuilder("", "");
        cb
                .addPackageName("info.smart_tools.smartactors.core")
                .addImport("ClassBuilder")
                .addImport("Modifiers")
        .addClass()
                .setClassName("A")
                .setInherited("B")
                .setInterfaces("IB")
                .setInterfaces("IC").setClassModifier(Modifiers.PUBLIC)
                .next()
        .addField()
                .setModifier(Modifiers.PRIVATE)
                .setName("field1")
                .setType("String")
                .next()
        .addField()
                .setModifier(Modifiers.PRIVATE)
                .setName("field1")
                .setType("Integer")
                .next()
        .addConstructor()
                .setModifier(Modifiers.PUBLIC)
                .setExceptions("InvalidArgumentException")
                .setExceptions("Exception")
                .setParameters()
                        .setType("Integer")
                        .setName("value")
                        .next()
                .addStringToBody("int a = 0;")
                .next()
        .addConstructor()
                .setModifier(Modifiers.PROTECTED)
                .setExceptions("InvalidArgumentException")
                .setExceptions("Exception")
                .setParameters()
                        .setType("Integer")
                        .setName("value1")
                        .next()
                .setParameters()
                        .setType("String")
                        .setName("value2")
                        .next()
                .addStringToBody("int a = 0;")
                .next()
        .addMethod()
                .setModifier(Modifiers.PUBLIC)
                .setReturnType("String")
                .setName("getStringValue")
                .addParameter()
                        .setType("Integer")
                        .setName("v")
                        .next()
                .setExceptions("InvalidArgumentException")
                .addStringToBody("return \"abc\";")
                .next()
        .addMethod()
                .setModifier(Modifiers.CLASS_PROTECTED)
                .setReturnType("String")
                .setName("getStringValue")
                .addParameter()
                        .setType("Integer")
                        .setName("v1")
                        .next()
                .addParameter()
                        .setType("Float")
                        .setName("v2")
                        .next()
                .setExceptions("Error")
                .setExceptions("Exception")
                .addStringToBody("return \"abc\";");

        String result = cb.buildClass().toString();

        String given = "package info.smart_tools.smartactors.core;import ClassBuilder;import Modifiers;public class A extends B implements IB, IC {private String field1;private Integer field1;public A(Integer value) throws InvalidArgumentException, Exception  {int a = 0;}protected A(Integer value1, String value2) throws InvalidArgumentException, Exception  {int a = 0;}public String getStringValue(Integer v) throws InvalidArgumentException  {return \"abc\";} String getStringValue(Integer v1, Float v2) throws Error, Exception  {return \"abc\";}}";
        assertEquals(result, given);
    }


}
