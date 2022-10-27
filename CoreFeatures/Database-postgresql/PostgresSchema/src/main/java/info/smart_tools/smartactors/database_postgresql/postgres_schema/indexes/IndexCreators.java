package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PostgresFieldPath;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        put("fulltext", FulltextIndexWriter::resolveAdd);
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
     * @param options document describing index creation options
     * @throws Exception when something goes wrong
     */
    public static void writeCreateIndexes(Writer body, CollectionName collection, IObject options) throws Exception {
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
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        Object indexFields = null;
        try {
            IFieldName indexDefinitionField = IOC.resolve(fieldNameKey, indexType);
            indexFields = options.getValue(indexDefinitionField);
        } catch (ReadValueException e) {
            // ignoring absence of this index type definition
            return;
        }

        List<FieldPath> fieldPaths = new ArrayList<>();
        if (indexFields == null) {
            return;
            // ignoring absence of this index type definition
        }
        try {
            boolean pathFound = checkAndAddSingleFieldPath(fieldPaths, indexFields);
            if (!pathFound) {
                for (Object fieldName : (List) indexFields) {
                    pathFound = checkAndAddSingleFieldPath(fieldPaths, fieldName);
                    if (!pathFound) {
                        throw new QueryBuildException("Bad options for creating index task: field data need to be a string, an IObject or a list");
                    }
                }
            }
        } catch (ClassCastException e) {
            throw new QueryBuildException("Bad options for creating index task: field data need to be a string, an IObject or a list", e);
        }


        INDEX_WRITERS.get(indexType).resolve(options).write(body, collection, fieldPaths);
    }

    private static void writeOrderedIndex(final Writer body, final CollectionName collection,
                                          final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ");
            body.write(collection.toString());
            body.write("_");
            body.write(field.getId());
            body.write("_ordered_index ON ");
            body.write(collection.toString());
            body.write(" USING BTREE ((");
            body.write(field.toSQL());
            body.write("));\n");
        }
    }

    private static void writeDatetimeIndex(final Writer body, final CollectionName collection,
                                           final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ");
            body.write(collection.toString());
            body.write("_");
            body.write(field.getId());
            body.write("_datetime_index ON ");
            body.write(collection.toString());
            body.write(" USING BTREE ((parse_timestamp_immutable(");
            body.write(field.toSQL());
            body.write(")));\n");
        }
    }

    private static void writeTagsIndex(final Writer body, final CollectionName collection,
                                       final List<FieldPath> fields) throws IOException {
        for (FieldPath field : fields) {
            body.write("CREATE INDEX ");
            body.write(collection.toString());
            body.write("_");
            body.write(field.getId());
            body.write("_tags_index ON ");
            body.write(collection.toString());
            body.write(" USING GIN ((");
            body.write(field.toSQL());
            body.write("));\n");
        }
    }

    private static boolean checkAndAddSingleFieldPath(List<FieldPath> fieldPaths, final Object fieldPathData) throws QueryBuildException {
        if (fieldPathData instanceof String) {
            fieldPaths.add(PostgresFieldPath.fromString((String) fieldPathData));
            return true;
        } else if (fieldPathData instanceof IObject) {
            try {
                IObject iObjectFieldPathData = (IObject) fieldPathData;
                IKey fieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
                IFieldName nameFN = IOC.resolve(fieldNameKey, "fieldName");
                IFieldName typeFN = IOC.resolve(fieldNameKey, "type");
                String name = (String) iObjectFieldPathData.getValue(nameFN);
                String type = (String) iObjectFieldPathData.getValue(typeFN);
                fieldPaths.add(PostgresFieldPath.fromStringAndType(name, type));
                return true;
            } catch (ResolutionException e) {
                throw new QueryBuildException("Cannot resolve dependency while building query ", e);
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new QueryBuildException("Cannot read value from field name options", e);
            } catch (NullPointerException e) {
                throw new QueryBuildException("Incorrect format of create index options", e);
            }
        }
        return false;
    }
}