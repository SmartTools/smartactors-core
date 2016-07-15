package info.smart_tools.smartactors.core.db_tasks.wrappers.search;

import info.smart_tools.smartactors.core.db_tasks.wrappers.IDBTaskMessage;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;

/**
 * Interface for message containing incoming search query to database.
 *
 * Criteria language ("criteria" field contents) rules:
 * <pre>
 *     criteria ::= criteriaGroup
 *
 *     // criteriaGroup which has no compositionType at left side is interpreted as a "$and" criteria group.
 *     criteriaGroup ::= [criteriaGroup, ...]
 *     criteriaGroup ::= {criteriaPair, ...}
 *
 *     // Works only outside a field context.
 *     // Creates a field context with field fieldName for nested criteriaGroup.
 *     criteriaPair ::= fieldName : criteriaGroup
 *
 *     // Works only inside of field context.
 *     // Parameter name must be a unique.
 *
 *     // General criteriaPair format.
 *     criteriaPair ::= constraintName : parameterName
 *
 *     // Format work only for "$isNull".
 *     criteriaPair ::= constraintName : parameterValue
 *
 *     // Format only for "$in".
 *     criteriaPair ::= constraintName : ["parameterName", sizeOfInArray]
 *
 *     // Works everywhere.
 *     criteriaPair ::= compositionType : criteriaGroup
 *
 *     constraintName ::= "$eq" | "$gt" | "$lt" | "$tagSet" | "$tagAll" | "$tagAny"
 *
 *     // "$not" actually works as NAND (because right part should always be a criteriaGroup.
 *     compositionType ::= "$or" | "$and" | "$not"
 * </pre>
 *
 * Parameters language ("parameters" field contents) rules:
 * <pre>
 *     parameters ::= parametersGroup
 *
 *     parametersGroup ::= {parameterPair, ...}
 *
 *     // parameterName is name from criteria in the criteriaPair.
 *     // parameterValue is a value for parameter with that name.
 *     // General parameterPair format.
 *     parameterPair ::= parameterName : parameterValue
 *
 *     // Format only for "$in".
 *     // Values number must not be more than declared array size in criteriaPair for "$in",
 *     // but may be a less.
 *     parameterPair ::= parameterName : [val1, val2, val3, ...]
 *
 * </pre>
 *
 * Example a part of the search query:
 * <code>
 *     {
 *         "criteria" : {
 *             "id" : { "$eq" : "idParamName" },
 *             "middleName" : { "$isNull" : "false" }
 *             "passportSerialNumber" : ["serialNumberParamName", 4]
 *         },
 *         "parameters" : {
 *             "idParamName" : 24,
 *             "serialNumberParamName" : [4212, 5141, 2134] // may be a less than sizeOfInArray.
 *         }
 *     }
 * </code>
 *
 */
public interface ISearchMessage extends IDBTaskMessage {
    /**
     * @return criteria by which search query.
     * @exception ReadValueException when error of reading search criteria in the message.
     */
    Object getCriteria() throws ReadValueException;

    /**
     * @return parameters which declared in the criteria.
     * @exception ReadValueException when error of reading search parameters in the message.
     */
    IObject getParameters() throws ReadValueException;

    /**
     * @param parameters - parameters which declared in the criteria.
     * @exception ChangeValueException when error of writing search parameters in the message.
     */
    void setParameters(IObject parameters) throws ChangeValueException;

    /**
     * @param criteria - criteria by which search query.
     * @exception ChangeValueException when error of writing search criteria in the message.
     */
    void setCriteria(Object criteria) throws ChangeValueException;

    /**
     * @return number of pages in searching query.
     * @exception ReadValueException when error of reading page size in the message.
     */
    Integer getPageSize() throws ReadValueException;

    /**
     * @param pageSize - number of pages in searching query.
     * @exception ChangeValueException when error of writing page size in the message.
     */
    void setPageSize(Integer pageSize) throws ChangeValueException;

    /**
     * @return page number at which to start searching.
     * @exception ReadValueException when error of reading page number in the message.
     */
    Integer getPageNumber() throws ReadValueException;

    /**
     * @param pageNumber page number at which to start searching.
     * @exception ChangeValueException when error of writing page number in the message.
     */
    void setPageNumber(Integer pageNumber) throws ChangeValueException;

    /**
     * @return order of search result list.
     * @exception ReadValueException when error of reading search result order in the message.
     */
    List<IObject> getOrderBy() throws ReadValueException;

    /**
     * @param order order of search result list.
     * @exception ChangeValueException when error of writing search result order in the message.
     */
    void setOrderBy(List<IObject> order) throws ChangeValueException;

    /**
     * @param object the list of found document.
     * @exception ChangeValueException when error of writing search result in the message.
     */
    void setSearchResult(List<IObject> object) throws ChangeValueException;

    /**
     *
     * @return the list of found document.
     * @exception ReadValueException when error of reading search result in the message.
     */
    List<IObject> getSearchResult() throws ReadValueException;
}
