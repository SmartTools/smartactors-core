package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

/**
 * Valid field path for Postgres database to represent path in jsonb document.
 * If it's the path in
 * {@see FiledPath} {@link FieldPath}
 */
public final class PostgresFieldPath implements FieldPath {
    private String path;

    private PostgresFieldPath(final String[] parts) {
        this.path = String.format("%s#>\'{%s}\'",
                "document",
                String.join(",", parts));
    }

    private PostgresFieldPath(final String column, final String castFunction) {
        this.path = String.format("%s(%s)", castFunction, column);
    }

    /**
     * {@see FieldPath#toSQL()} {@link FieldPath#toSQL()}
     * @return valid representation of field path.
     */
    public String toSQL() {
        return this.path;
    }

    public static PostgresFieldPath fromString(final String path)
            throws QueryBuildException {
        if (!FieldPath.isValid(path)) {
            throw new QueryBuildException("Invalid field path: " + path);
        }

        // TODO: add a special support for ID column (it depends on collection name)
//        if (path.equals("id")) {
//            return new PostgresFieldPath(PostgresSchema.ID_COLUMN, Schema.ID_TO_JSONB_CAST_FUNCTION);
//        }

        return new PostgresFieldPath(FieldPath.splitParts(path));
    }
}
