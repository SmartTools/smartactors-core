package info.smart_tools.smartactors.core.db_task.search.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.sql_commons.ConditionsResolverBase;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;

/**
 * {@see ConditionsResolverBase} {@link ConditionsResolverBase}.
 */
final class ConditionsWriterResolver extends ConditionsResolverBase {

    private ConditionsWriterResolver() {
        Operators.addAll(this);
    }

    /**
     * Factory method for creating a new instance.
     * @return a new instance of <pre>ConditionsWriterResolver</pre>.
     */
    public static ConditionsWriterResolver create() {
        return new ConditionsWriterResolver();
    }

    /**
     * Resolves valid field path for psql db.
     *
     * @param name - name of field path for psql db.
     *
     * @return a <pre>FieldPath</pre> object with valid field path for psql db.
     *
     * @throws QueryBuildException when invalid name of field path for psql db.
     */
    public FieldPath resolveFieldName(final String name) throws QueryBuildException {
        return PSQLFieldPath.fromString(name);
    }
}
