package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.util.HashMap;
import java.util.Map;

public abstract class ConditionsResolverBase implements QueryConditionWriterResolver {
    private Map<String,QueryConditionWriter> operatorWriters = new HashMap<>();
    protected String defaultCompositionOperator = "$and";

    public void addOperator(String name, QueryConditionWriter writer) {
        operatorWriters.put(name,writer);
    }

    public abstract FieldPath resolveFieldName(String name) throws QueryBuildException;

    public QueryConditionWriter resolve(String name) throws QueryBuildException {
        if(name == null) {
            return this.resolve(defaultCompositionOperator);
        }

        QueryConditionWriter operatorWriter = operatorWriters.get(name);

        if(operatorWriter != null) {
            return operatorWriter;
        }

        return resolveFieldWriter(name);
    }

    protected QueryConditionWriter resolveFieldWriter(String fieldName)
        throws QueryBuildException {
        final FieldPath path = this.resolveFieldName(fieldName);

        return (query, resolver, contextFieldName, queryParameter) -> {
            if(contextFieldName != null) {
                throw new QueryBuildException("Field names not allowed inside of field context.");
            }

            this.resolve(null).write(query,resolver,path,queryParameter);
        };
    }

    {
        addOperator("$and",Conditions::writeAndCondition);
        addOperator("$or",Conditions::writeOrCondition);
        addOperator("$not",Conditions::writeNotCondition);
    }
}
