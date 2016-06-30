package info.smart_tools.smartactors.core.db_task.search.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.psql.Schema;

import javax.annotation.Nonnull;

/**
 * {@see FiledPath} {@link FieldPath}
 * Valid field path for psql database.
 */
public class PSQLFieldPath implements FieldPath {
    private String path;

    private PSQLFieldPath(final String[] parts) {
        this.path = String.format("%s#>\'{%s}\'",
                "document",
                String.join(",", parts));
    }

    private PSQLFieldPath(final String column, final String castFunction) {
        this.path = String.format("%s(%s)", castFunction, column);
    }

    /**
     * {@see FieldPath#getSQLRepresentation()} {@link FieldPath#getSQLRepresentation()}
     * @return valid representation of field path.
     */
    public String getSQLRepresentation() {
        return this.path;
    }

    public static PSQLFieldPath fromString(@Nonnull final String path)
            throws QueryBuildException {
        if (!FieldPath.isValid(path)) {
            throw new QueryBuildException("Invalid field path: " + path);
        }

        if (path.equals("id")) {
            return new PSQLFieldPath(Schema.ID_COLUMN_NAME, Schema.ID_TO_JSONB_CAST_FUNCTION);
        }

        return new PSQLFieldPath(FieldPath.splitParts(path));
    }
}
