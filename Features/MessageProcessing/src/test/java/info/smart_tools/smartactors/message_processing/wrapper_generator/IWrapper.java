package info.smart_tools.smartactors.message_processing.wrapper_generator;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Test interface for generation wrapper
 */
public interface IWrapper {

    Integer getIntValue()
            throws ReadValueException;

    void setIntValue(int a)
            throws ChangeValueException;

    void transform(Integer i)
            throws ChangeValueException;

    TestClass getTestClassValue()
            throws ReadValueException;

    void setTestClassValue(TestClass t)
            throws ChangeValueException;

    List<TestClass> getListOfTestClasses()
            throws ReadValueException;

    void setListOfTestClasses(List<TestClass> list)
            throws ChangeValueException;

    IInnerWrapper wrappedIObject()
            throws ReadValueException;

    void wrappedIObject(IInnerWrapper wrappedIObject)
            throws ChangeValueException;
}
