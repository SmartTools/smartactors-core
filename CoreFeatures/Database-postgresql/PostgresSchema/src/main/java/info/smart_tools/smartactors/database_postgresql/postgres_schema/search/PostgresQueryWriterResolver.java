package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves writers of query for Postgres database and jsonb documents.
 */
public class PostgresQueryWriterResolver implements QueryWriterResolver {

    /**
     * The set of all registered query writers.
     */
    private Map<String, QueryWriter> queryWriters = new HashMap<>();

    /**
     * Default condition to join other operators.
     */
    private static final String DEFAULT_COMPOSITION_OPERATOR = "$and";

    /**
     * Creates the resolver to convert criteria into request to jsonb documents.
     */
    public PostgresQueryWriterResolver() {
        addQueryWriter("$and", Conditions::writeAndCondition);
        addQueryWriter("$or", Conditions::writeOrCondition);
        addQueryWriter("$not", Conditions::writeNotCondition);
        Operators.addAll(this);
    }

    /**
     * Registers new query writer.
     * @param name name of the operator in the query criteria, for example '$eq' or '$and'
     * @param writer new writer to register
     */
    public void addQueryWriter(final String name, final QueryWriter writer) {
        queryWriters.put(name, writer);
    }

    @Override
    public QueryWriter resolve(final String name) throws QueryBuildException {
        if (name == null) {
            return this.resolve(DEFAULT_COMPOSITION_OPERATOR);
        }

        QueryWriter operatorWriter = queryWriters.get(name);

        if (operatorWriter != null) {
            return operatorWriter;
        }

        return resolveFieldWriter(name);
    }

    /**
     * Creates the writer for the field name to access the field in jsonb document.
     * @param fieldName name of the field from the criteria, like 'a.b'
     * @return the writer which writes field access in jsonb format, like 'document#>{a,b}'
     * @throws QueryBuildException if something goes wrong
     */
    private QueryWriter resolveFieldWriter(final String fieldName)
        throws QueryBuildException {

        return (query, resolver, contextFieldName, queryParameter) -> {
            if (contextFieldName != null) {
                throw new QueryBuildException("Field names not allowed inside of field context");
            }
            this.resolve(null).write(query, resolver, fieldName, queryParameter);
        };
    }

}
