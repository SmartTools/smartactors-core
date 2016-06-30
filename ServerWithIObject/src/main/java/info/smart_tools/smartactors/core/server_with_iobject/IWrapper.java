package info.smart_tools.smartactors.core.server_with_iobject;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Demonstration interface for generation wrapper
 */
public interface IWrapper {

    /**
     * Getter for int value
     * @return instance of {@link Integer}
     * @throws ReadValueException if any errors occurred
     */
    Integer getIntValue() throws ReadValueException;

    /**
     * Setter for int value
     * @param a instance of {@link Integer}
     * @throws ChangeValueException if any errors occurred
     */
    void setIntValue(int a) throws ChangeValueException;

    /**
     * Getter for string value
     * @return instance of {@link String}
     * @throws ReadValueException if any errors occurred
     */
    String getStringValue() throws ReadValueException;

    /**
     * Setter for string value
     * @param s instance of {@link String}
     * @throws ChangeValueException if any errors occurred
     */
    void setStringValue(String s) throws ChangeValueException;

    /**
     * Getter for list of int
     * @return instance of {@link List<Integer>}
     * @throws ReadValueException if any errors occurred
     */
    List<Integer> getListOfInt() throws ReadValueException;

    /**
     * Setter for list of integer
     * @param list instance of {@link List<Integer>}
     * @throws ChangeValueException if any errors occurred
     */
    void setListOfInt(List<Integer> list) throws ChangeValueException;

    /**
     * Getter for list of string
     * @return instance of {@link List<String>}
     * @throws ReadValueException if any errors occurred
     */
    List<String> getListOfString() throws ReadValueException;

    /**
     * Setter for list string
     * @param list instance of {@link List<String>}
     * @throws ChangeValueException if any errors occurred
     */
    void setListOfString(List<String> list) throws ChangeValueException;

    /**
     * Getter for bool value
     * @return instance of {@link Boolean}
     * @throws ReadValueException if any errors occurred
     */
    Boolean getBoolValue() throws ReadValueException;

    /**
     * Setter for bool value
     * @param value instance of {@link Boolean}
     * @throws ChangeValueException if any errors occurred
     */
    void setBoolValue(boolean value) throws ChangeValueException;

    /**
     * Getter for IObject instance
     * @return instance of {@link IObject}
     * @throws ReadValueException if any errors occurred
     */
    IObject getIObject() throws ReadValueException;

    /**
     * Setter for IObject instance
     * @param iObject instance of {@link IObject}
     * @throws ChangeValueException if any errors occurred
     */
    void setIObject(IObject iObject) throws ChangeValueException;
}
