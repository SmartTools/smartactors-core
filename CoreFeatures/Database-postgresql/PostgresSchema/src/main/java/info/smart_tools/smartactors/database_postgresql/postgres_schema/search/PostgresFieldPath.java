package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;

/**
 * Valid field path for Postgres database to represent path in jsonb document.
 * If it's the path in
 * {@see FiledPath} {@link FieldPath}
 */
public class PostgresFieldPath implements FieldPath {
    private String path;
    private String id;

    private PostgresFieldPath(final String[] parts) {
        this.path = String.format("%s#>\'{%s}\'",
                "document",
                String.join(",", parts));
        this.id = String.join("_", parts);
    }

    private PostgresFieldPath(final String column, final String castFunction) {
        this.path = String.format("%s(%s)", castFunction, column);
        this.id = String.format("%s_%s", castFunction, column);
    }

    private PostgresFieldPath(final String[] parts, final String typeToCast) {
        this.path = String.format("(%s#>>\'{%s}\')::%s",
                "document",
                String.join(",", parts),
                typeToCast);
        this.id = String.join("_", parts);
    }

    /**
     * {@see FieldPath#toSQL()} {@link FieldPath#toSQL()}
     * @return valid representation of field path.
     */
    public String toSQL() {
        return this.path;
    }

    /**
     * {@see FieldPath#getId()} {@link FieldPath#getId()}
     * @return valid representation of field path id.
     */
    public String getId() {
        return this.id;
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

    public static PostgresFieldPath fromStringAndType(final String path, final String typeToCast)
            throws QueryBuildException {
        if (!FieldPath.isValid(path)) {
            throw new QueryBuildException("Invalid field path: " + path);
        }

        return new PostgresFieldPath(FieldPath.splitParts(path), typeToCast);
    }
}
