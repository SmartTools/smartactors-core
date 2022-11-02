package info.smart_tools.smartactors.database.sql_commons;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.interfaces.SQLQueryParameterSetter;

import java.util.List;

/**
 *  Writes a part of SQL statement search conditions.
 */
@FunctionalInterface
public interface QueryConditionWriter {
    void write(QueryStatement query, QueryConditionWriterResolver resolver, FieldPath contextFieldPath,
               Object queryParameter, List<SQLQueryParameterSetter> setters) throws QueryBuildException;
}
