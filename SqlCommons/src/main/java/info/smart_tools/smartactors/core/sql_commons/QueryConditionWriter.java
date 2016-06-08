package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

/**
 *  Writes a part of SQL statement search conditions.
 */
@FunctionalInterface
public interface QueryConditionWriter {
    void write(QueryStatement query, QueryConditionWriterResolver resolver,
               FieldPath contextFieldPath, Object queryParameter) throws QueryBuildException;
}
