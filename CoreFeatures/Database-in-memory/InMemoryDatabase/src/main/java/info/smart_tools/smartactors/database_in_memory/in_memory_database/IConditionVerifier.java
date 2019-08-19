package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Class, that should verify, is IObject satisfy condition
 */
public interface IConditionVerifier {

    /**
     * Method for verify is
     * @param condition condition, that should be verified
     * @param document  document, that should be verified
     * @return true if the condition is applicable to the document
     */
    boolean verify(IObject condition, IObject document);
}
