package info.smart_tools.smartactors.core.db_task.create_collection.psql;

/**
 * Utility class with constants needed for create new collection
 */
class Schema {

    static final String ID_COLUMN_NAME = "id";
    static final String DOCUMENT_COLUMN_NAME = "document";

    static final String ID_COLUMN_SQL_TYPE = "BIGSERIAL";

    // Dictionary for Full Text Search
    static final String FTS_DICTIONARY = "russian";
}
