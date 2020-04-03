package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

/**
 * Resolves a {@link QueryWriter} for specific condition object key.
 */
public interface QueryWriterResolver {

    /**
     * Returns the writer to write the specific query condition identified by the key
     * @param conditionKey key in key-value pair of condition object. May be an operator name ('$and', '$eq', '$lt', etc..),
     *                     field path ('parent.child', for example) or null for default operator
     * @return ConditionWriter found for this condition key
     * @throws QueryBuildException if something goes wrong
     */
    QueryWriter resolve(String conditionKey) throws QueryBuildException;

}
