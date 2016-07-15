package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.util.List;

/**
 *  Writes a part of SQL statement search conditions.
 */
@FunctionalInterface
public interface QueryConditionWriter {
    void write(QueryStatement query, QueryConditionResolver resolver, FieldPath contextFieldPath,
               Object queryParameter, List<IDeclaredParam> parametersOrder) throws QueryBuildException;
}
