package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;
import java.util.Map;

/**
 * Test interface for generation wrapper
 */
public interface IWrapper {

    Integer getIntValue()
            throws ReadValueException;

    void setIntValue(int a)
            throws ChangeValueException;

    String getStringValue()
            throws ReadValueException;

    void setStringValue(String s)
            throws ChangeValueException;

    TestClass getTestClassValue()
            throws ReadValueException;

    void setTestClassValue(TestClass t)
            throws ChangeValueException;

    List<Integer> getListOfInt()
            throws ReadValueException;

    void setListOfInt(List<Integer> list)
            throws ChangeValueException;

    List<String> getListOfString()
            throws ReadValueException;

    void setListOfString(List<String> list)
            throws ChangeValueException;

    List<TestClass> getListOfTestClasses()
            throws ReadValueException;

    void setListOfTestClasses(List<TestClass> list)
            throws ChangeValueException;

    Boolean getBoolValue()
            throws ReadValueException;

    void setBoolValue(boolean value)
            throws ChangeValueException;

    IInnerWrapper getWrappedIObject()
            throws ReadValueException;

    void setWrappedIObject(IInnerWrapper wrappedIObject)
            throws ChangeValueException;

    IObject getIObject()
            throws ReadValueException;

    void setIObject(IObject iObject)
            throws ChangeValueException;

    Map<String, IInnerWrapper> getStringIInnerMap()
            throws ReadValueException;

    void setStringIInnerMap(Map<String, IInnerWrapper> map)
            throws ChangeValueException;
}
