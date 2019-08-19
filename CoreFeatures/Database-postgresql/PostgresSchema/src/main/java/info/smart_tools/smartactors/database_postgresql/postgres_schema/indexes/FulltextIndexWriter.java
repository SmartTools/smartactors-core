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
final class FulltextIndexWriter implements IndexWriter {

    static IndexWriter resolve(final IObject options) throws Exception {
        IKey fieldNameKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
        IFieldName languageField = IOC.resolve(fieldNameKey, "language");
        String language = (String) options.getValue(languageField);
        if (language == null) {
            throw new QueryBuildException("Language is required for fulltext index");
        }
        return new FulltextIndexWriter(language);
    }

    private final String language;

    private FulltextIndexWriter(final String language) {
        this.language = language;
    }

    @Override
    public void write(final Writer body, final CollectionName collection, final List<FieldPath> fields) throws IOException {
        writeCreateIndex(body, collection);
        writeCreateFunction(body, collection, fields);
        writeCreateTrigger(body, collection);
    }

    private void writeCreateIndex(final Writer body, final CollectionName collection) throws IOException {
        body.write("CREATE INDEX ON ");
        body.write(collection.toString());
        body.write(" USING GIN (");
        body.write(PostgresSchema.FULLTEXT_COLUMN);
        body.write(");\n");
    }

    private void writeCreateFunction(final Writer body, final CollectionName collection, final List<FieldPath> fields) throws IOException {
        body.write("CREATE FUNCTION ");
        body.write(collection.toString());
        body.write("_fulltext_update_trigger() RETURNS trigger AS $$\n");
        body.write("begin\n");
        body.write("new.");
        body.write(PostgresSchema.FULLTEXT_COLUMN);
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

    private void writeCreateTrigger(final Writer body, final CollectionName collection) throws IOException {
        String collectionName = collection.toString();
        body.write("CREATE TRIGGER ");
        body.write(collectionName);
        body.write("_fulltext_update_trigger BEFORE INSERT OR UPDATE ON ");
        body.write(collectionName);
        body.write(" FOR EACH ROW EXECUTE PROCEDURE ");
        body.write(collectionName);
        body.write("_fulltext_update_trigger();\n");
    }

}
