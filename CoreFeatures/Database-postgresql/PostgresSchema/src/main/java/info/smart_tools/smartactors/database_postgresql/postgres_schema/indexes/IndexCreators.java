package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PostgresFieldPath;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * A set of classes to write CREATE INDEX SQL statements.
 */
public class IndexCreators {

    /**
     * Set of index creation functions.
     */
    private static final Map<String, IndexWriterResolver> INDEX_WRITERS = new HashMap<String, IndexWriterResolver>() {{
        put("ordered", (indexDefinition) -> IndexCreators::writeOrderedIndex);
        put("datetime", (indexDefinition) -> IndexCreators::writeDatetimeIndex);
        put("tags", (indexDefinition) -> IndexCreators::writeTagsIndex);
        put("fulltext", FulltextIndexWriter::resolve);
    }};

    /**
     * Private constructor to avoid instantiation.
     */
    private IndexCreators() {
    }

    /**
     * Writes CREATE INDEX statements to the SQL statement body.
     * @param body where to write SQL
     * @param collection name of the collection
     * @param options document describing create collection options
     * @throws Exception when something goes wrong
     */
    public static void writeIndexes(Writer body, CollectionName collection, IObject options) throws Exception {
        try {
            if (options == null) {
                // no indexes definition, ignoring
                return;
            }
            for (String indexType : INDEX_WRITERS.keySet()) {
                writeCreateIndex(indexType, body, collection, options);
            }
        } catch (ReadValueException e) {
            // no indexes definition, ignoring
        }
    }

    private static void writeCreateIndex(final String indexType, final Writer body, final CollectionName collection, IObject options)
            throws Exception {
        IKey fieldNameKey = Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Object indexFields = null;
        try {
            IFieldName indexDefinitionField = IOC.resolve(fieldNameKey, indexType);
            indexFields = options.getValue(indexDefinitionField);
        } catch (ReadValueException e) {
            // ignoring absence of this index type definition
            return;
        }

        List<FieldPath> fieldPaths = new ArrayList<>();
        if (indexFields instanceof String) {
            fieldPaths.add(PostgresFieldPath.fromString((String) indexFields));
        } else if (indexFields instanceof List) {
            for (Object fieldName : (List) indexFields) {
                fieldPaths.add(PostgresFieldPath.fromString((String) fieldName));
            }
        } else if (indexFields == null) {
            // ignoring absence of this index type definition
            return;
        } else {
            throw new QueryBuildException("Unknown index definition for " + indexType + ": " + indexFields);
        }

        INDEX_WRITERS.get(indexType).resolve(options).write(body, collection, fieldPaths);
    }

    private static void writeOrderedIndex(final Writer body, final CollectionName collection,
                                          final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ON ");
            body.write(collection.toString());
            body.write(" USING BTREE ((");
            body.write(field.toSQL());
            body.write("));\n");
        }

    }

    private static void writeDatetimeIndex(final Writer body, final CollectionName collection,
                                           final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ON ");
            body.write(collection.toString());
            body.write(" USING BTREE (");
            body.write("(parse_timestamp_immutable(");
            body.write(field.toSQL());
            body.write("))");
            body.write(");\n");
        }
    }

    private static void writeTagsIndex(final Writer body, final CollectionName collection,
                                       final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ON ");
            body.write(collection.toString());
            body.write(" USING GIN ((");
            body.write(field.toSQL());
            body.write("));\n");
        }
    }
}
