package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.PostgresSchema;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * Writes fulltext index and appropriate trigger definitions.
 */
class FulltextIndexWriter {

    static IndexWriter resolveAdd(IObject options) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        IFieldName languageField = IOC.resolve(fieldNameKey, "language");
        String language = (String) options.getValue(languageField);
        if (language == null) {
            language = PostgresSchema.DEFAULT_FTS_DICTIONARY;
        }
        return (new FulltextIndexWriter(language))::writeAdd;
    }

    static IndexWriter resolveDrop(IObject options) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        IFieldName languageField = IOC.resolve(fieldNameKey, "language");
        String language = (String) options.getValue(languageField);
        if (language == null) {
            language = PostgresSchema.DEFAULT_FTS_DICTIONARY;
        }
        return (new FulltextIndexWriter(language))::writeDrop;
    }

    private final String language;
    private final String columnName;

    private FulltextIndexWriter(final String language) {
        this.language = language;
        this.columnName = PostgresSchema.FULLTEXT_COLUMN + "_" + language;
    }

    private void writeAdd(final Writer body, final CollectionName collection, final List<FieldPath> fields)
            throws IOException {
        writeCreateIndex(body, collection);
        writeCreateFunction(body, collection, fields);
        writeCreateTrigger(body, collection);
    }

    private void writeDrop(final Writer body, final CollectionName collection, final List<FieldPath> fields)
            throws IOException {
        writeDropTrigger(body, collection);
        writeDropFunction(body, collection, fields);
        writeDropIndex(body, collection);
    }

    private void writeCreateIndex(final Writer body, final CollectionName collection) throws IOException {
        body.write("CREATE INDEX ");
        body.write(collection.toString());
        body.write("_");
        body.write(columnName);
        body.write("_index ON ");
        body.write(collection.toString());
        body.write(" USING GIN (");
        body.write(columnName);
        body.write(");\n");
    }

    private void writeDropIndex(final Writer body, final CollectionName collection) throws IOException {
        body.write("DROP INDEX ");
        body.write(collection.toString());
        body.write("_");
        body.write(columnName);
        body.write("_index;\n");
    }

    private void writeCreateFunction(final Writer body, final CollectionName collection, final List<FieldPath> fields) throws IOException {
        body.write("CREATE FUNCTION ");
        body.write(collection.toString());
        body.write("_fulltext_");
        body.write(language);
        body.write("_update_trigger() RETURNS trigger AS $$\n");
        body.write("begin\n");
        body.write("new.");
        body.write(columnName);
        body.write(" := ");
        body.write("to_tsvector('");
        body.write(language);   // TODO: avoid SQL injection
        body.write("', ");
        Iterator<FieldPath> i = fields.iterator();
        while (i.hasNext()) {
            FieldPath field = i.next();
            body.write("coalesce((new.");
            body.write(field.toSQL());
            body.write(")::text,'')");
            if (i.hasNext()) {
                body.write(" || ' ' || ");
            }
        }
        body.write(");\n");
        body.write("return new;\n");
        body.write("end\n");
        body.write("$$ LANGUAGE plpgsql;\n");
    }

    private void writeDropFunction(final Writer body, final CollectionName collection, final List<FieldPath> fields) throws IOException {
        body.write("DROP FUNCTION ");
        body.write(collection.toString());
        body.write("_fulltext_");
        body.write(language);
        body.write("_update_trigger();\n");
    }

    private void writeCreateTrigger(final Writer body, final CollectionName collection) throws IOException {
        String collectionName = collection.toString();
        body.write("CREATE TRIGGER ");
        body.write(collectionName);
        body.write("_fulltext_");
        body.write(language);
        body.write("_update_trigger BEFORE INSERT OR UPDATE ON ");
        body.write(collectionName);
        body.write(" FOR EACH ROW EXECUTE PROCEDURE ");
        body.write(collectionName);
        body.write("_fulltext_");
        body.write(language);
        body.write("_update_trigger();\n");
    }

    private void writeDropTrigger(final Writer body, final CollectionName collection) throws IOException {
        String collectionName = collection.toString();
        body.write("DROP TRIGGER ");
        body.write(collectionName);
        body.write("_fulltext_");
        body.write(language);
        body.write("_update_trigger ON ");
        body.write(collectionName);
        body.write(";\n");
    }

}
