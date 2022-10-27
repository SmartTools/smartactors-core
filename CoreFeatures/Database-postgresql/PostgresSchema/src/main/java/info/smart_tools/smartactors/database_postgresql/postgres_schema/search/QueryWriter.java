package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;

/**
 * Writes a part of SQL statement for the search condition.
 */
@FunctionalInterface
public interface QueryWriter {

    /**
     * Writes the part of the SQL statement body
     * @param query query to which body to write the condition and where to add parameter setters
     * @param resolver the resolver to resolve nested conditions
     * @param contextFieldPath unparsed current document field path
     * @param queryParameter currently processing query parameter
     * @throws QueryBuildException when something goes wrong
     */
    void write(QueryStatement query, QueryWriterResolver resolver, String contextFieldPath,
               Object queryParameter) throws QueryBuildException;

}
