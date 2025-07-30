package info.smart_tools.smartactors.database_postgresql.postgres_schema;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.database_postgresql.postgres_schema.search.FieldPath;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * A set of methods to write some SQL statements during the collection/table creation.
 */
public final class CreateClauses {

    private static final Properties INIT_PROPS = new Properties();
    static {
        try (InputStream src = CreateClauses.class.getResourceAsStream("db-init.properties")) {
            INIT_PROPS.load(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor to avoid instantiation.
     */
    private CreateClauses() {
    }

    /**
     * Writes a set of common functions.
     * @param body body of SQL query to write
     * @throws IOException if failed to write to body
     */
    public static void writeFunctions(final Writer body) throws IOException {
        for (Object key : INIT_PROPS.keySet()) {
            body.write(INIT_PROPS.getProperty((String) key));
            body.write("\n");
        }
    }

    /**
     * Writes the primary key definition.
     * @param body body of SQL query to write
     * @param collection name of the collection/table
     * @throws IOException if write to body is not possible
     * @throws QueryBuildException if there is a syntax error
     */
    public static void writePrimaryKey(final Writer body, final CollectionName collection) throws IOException, QueryBuildException {
        String collectionName = collection.toString();
        FieldPath idPath = PostgresSchema.getIdFieldPath(collection);
        body.write("CREATE UNIQUE INDEX ");
        body.write(collectionName);
        body.write("_pkey ON ");
        body.write(collectionName);
        body.write(" USING BTREE ((");
        body.write(idPath.toSQL());
        body.write("));\n");
    }

    /**
     * Writes definition of full text column, i.e. ", fulltext_english tsvector".
     * @param body SQL query body to write
     * @param options collection create options to check for fulltext index
     * @throws ResolutionException if failed to resolve field names from IOC
     * @throws InvalidArgumentException if failed to get fulltext field from options
     * @throws IOException if failed to write the body
     */
    public static void writeFullTextColumn(final Writer body, final IObject options)
            throws ResolutionException, InvalidArgumentException, IOException {
        if (options == null) {
            // ignoring absence of fulltext option
            return;
        }
        String fullTextLanguage = PostgresSchema.DEFAULT_FTS_DICTIONARY;
        try {
            IKey fieldKey = Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName");
            IFieldName fullTextField = IOC.resolve(fieldKey, "fulltext");
            Object fullTextDefinition = options.getValue(fullTextField);
            if (fullTextDefinition == null) {
                // ignoring absence of fulltext option
                return;
            }
            IFieldName languageField = IOC.resolve(fieldKey, "language");
            String language = (String) options.getValue(languageField);
            if (language != null) {
                fullTextLanguage = language;
            }
        } catch (ReadValueException e) {
            // ignoring absence of fulltext option
        }
        body.write(", ");
        body.write(PostgresSchema.FULLTEXT_COLUMN + "_" + fullTextLanguage);
        body.write(" tsvector");
    }
}
