package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;

import java.io.Writer;
import java.util.List;

/**
 * An interface for a function or class who able to write an SQL statement part for the end of the CREATE INDEX.
 */
interface IndexWriter {
    void write(Writer body, CollectionName collection, List<FieldPath> fields) throws Exception;
}
