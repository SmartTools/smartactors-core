package info.smart_tools.smartactors.database_postgresql.postgres_schema;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.PostgresFieldPath;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A set of methods to write some SQL statements during the collection/table altering.
 */
final class AlterClauses {

    /**
     * Private constructor to prevent instantiation.
     */
    private AlterClauses() {
    }

    /**
     * Writes expression for fulltext column creation and update,
     * i.e. "ALTER TABLE collectionName ADD COLUMN ...; UPDATE collectionName SET fulltext_english := to_tsvector('english', ...)".
     * @param body SQL query body to write
     * @throws IOException if failed to write the body
     */
    static void writeAddFulltextColumn(final Writer body, final CollectionName collection, final IObject options)
            throws QueryBuildException, IOException {
        if (options == null) {
            // if no fulltext option just skip
            return;
        }
        String language;
        List<FieldPath> fields;
        try {
            IKey fieldKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

            IFieldName fulltextDefinitionField = IOC.resolve(fieldKey, "fulltext");
            Object fulltextFields = options.getValue(fulltextDefinitionField);
            if (fulltextFields == null) {
                // if no fulltext option just skip
                return;
            }

            IFieldName languageField = IOC.resolve(fieldKey, "language");
            language = (String) options.getValue(languageField);
            if (language == null) {
                language = PostgresSchema.DEFAULT_FTS_DICTIONARY;
            }

            fields = new ArrayList<>();
            if (fulltextFields instanceof String) {
                fields.add(PostgresFieldPath.fromString((String) fulltextFields));
            } else if (fulltextFields instanceof List) {
                for (Object fieldName : (List) fulltextFields) {
                    if (fieldName instanceof String) {
                        fields.add(PostgresFieldPath.fromString((String) fieldName));
                    } else {
                        throw new QueryBuildException("Column name has non-String type.");
                    }
                }
            } else {
                throw new QueryBuildException("Unknown definition for 'fulltext' option: " + fulltextFields);
            }
        } catch (ResolutionException | InvalidArgumentException | ReadValueException e) {
            throw new QueryBuildException("Failed to build 'add fulltext' body", e);
        }

        body.write("ALTER TABLE ");
        body.write(collection.toString());
        body.write(" ADD COLUMN ");
        body.write(PostgresSchema.FULLTEXT_COLUMN);
        body.write("_");
        body.write(language);
        body.write(" tsvector DEFAULT NULL;\n");
        body.write("UPDATE ");
        body.write(collection.toString());
        body.write(" SET ");
        body.write(PostgresSchema.FULLTEXT_COLUMN);
        body.write("_");
        body.write(language);
        body.write(" := to_tsvector('");
        body.write(language);   // TODO: avoid SQL injection
        body.write("', ");
        Iterator<FieldPath> i = fields.iterator();
        while (i.hasNext()) {
            FieldPath field = i.next();
            body.write("coalesce((");
            body.write(field.toSQL());
            body.write(")::text,'')");
            if (i.hasNext()) {
                body.write(" || ' ' || ");
            }
        }
        body.write(");\n");
    }

    /**
     * Writes expression for fulltext column dropping, i.e. "ALTER TABLE collectionName DROP COLUMN ...
     * @param body SQL query body to write
     * @throws IOException if failed to write the body
     */
    static void writeDropFulltextColumn(final Writer body, final CollectionName collection, final IObject options)
            throws QueryBuildException, IOException {
        if (options == null) {
            return;
        }
        String language;
        try {
            IKey fieldKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");

            IFieldName fulltextDefinitionField = IOC.resolve(fieldKey, "fulltext");
            Object fulltextFields = options.getValue(fulltextDefinitionField);
            if (fulltextFields == null) {
                // if no fulltext option just skip
                return;
            }

            IFieldName languageField = IOC.resolve(fieldKey, "language");
            language = (String) options.getValue(languageField);
            if (language == null) {
                language = PostgresSchema.DEFAULT_FTS_DICTIONARY;
            }
        } catch (ResolutionException | InvalidArgumentException | ReadValueException e) {
            throw new QueryBuildException("Failed to build 'add fulltext' body", e);
        }

        body.write("ALTER TABLE ");
        body.write(collection.toString());
        body.write(" DROP COLUMN ");
        body.write(PostgresSchema.FULLTEXT_COLUMN);
        body.write("_");
        body.write(language);
        body.write(";\n");
    }
}
