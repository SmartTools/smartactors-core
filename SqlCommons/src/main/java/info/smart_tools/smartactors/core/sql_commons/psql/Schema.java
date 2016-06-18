package info.smart_tools.smartactors.core.sql_commons.psql;

public class Schema {
    public static final String ID_COLUMN_NAME = "id";
    public static final String DOCUMENT_COLUMN_NAME = "document";

    public static final String ID_COLUMN_SQL_TYPE = "BIGSERIAL";

    // Dictionary for Full Text Search
    public static final String FTS_DICTIONARY = "russian";

    // Function for building index on ID field
    public static final String ID_TO_JSONB_CAST_FUNCTION = "bigint_to_jsonb_immutable";
}
