package info.smart_tools.smartactors.database.sql_commons;


import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

/**
 *  Resolves a {@link QueryConditionWriter} for specific condition object key.
 */
public interface QueryConditionWriterResolver {
    /**
     *
     *  @param conditionKey key in key-value pair of condition object. May be an operator name,
     *                      field path or null for default operator.
     *  @return QueryConditionWriter found for this condition key.
     *  @throws QueryBuildException if any errors occurred
     */
    QueryConditionWriter resolve(String conditionKey) throws QueryBuildException;
}
