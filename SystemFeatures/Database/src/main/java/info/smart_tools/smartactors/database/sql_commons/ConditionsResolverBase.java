package info.smart_tools.smartactors.database.sql_commons;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

import java.util.HashMap;
import java.util.Map;

public abstract class ConditionsResolverBase implements QueryConditionWriterResolver {
    private Map<String, QueryConditionWriter> operatorWriters = new HashMap<>();
    protected String defaultCompositionOperator = "$and";

    public void addOperator(final String name, final QueryConditionWriter writer) {
        operatorWriters.put(name, writer);
    }

    public abstract FieldPath resolveFieldName(String name) throws QueryBuildException;

    public QueryConditionWriter resolve(final String name) throws QueryBuildException {
        if (name == null) {
            return this.resolve(defaultCompositionOperator);
        }

        QueryConditionWriter operatorWriter = operatorWriters.get(name);

        if (operatorWriter != null) {
            return operatorWriter;
        }

        return resolveFieldWriter(name);
    }

    protected QueryConditionWriter resolveFieldWriter(final String fieldName)
        throws QueryBuildException {
        final FieldPath path = this.resolveFieldName(fieldName);

        return (query, resolver, contextFieldName, queryParameter, setters) -> {
            if (contextFieldName != null) {
                throw new QueryBuildException("Field names not allowed inside of field context.");
            }

            this.resolve(null).write(query, resolver, path, queryParameter, setters);
        };
    }

    {
        addOperator("$and", Conditions::writeAndCondition);
        addOperator("$or", Conditions::writeOrCondition);
        addOperator("$not", Conditions::writeNotCondition);
    }
}
