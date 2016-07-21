package info.smart_tools.smartactors.core.db_tasks.psql.search;


import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_tasks.psql.search.utils.PSQLFieldPath;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PSQLFieldPathTest {

    @Test
    public void should_ResolvesPSQLFieldPath() throws QueryBuildException {
        String path = "testValidPath";
        FieldPath fieldPath = PSQLFieldPath.fromString(path);
        assertEquals(fieldPath.getSQLRepresentation(), "document#>'{testValidPath}'");
    }

    @Test(expected = QueryBuildException.class)
    public void should_ThrowsException() throws QueryBuildException {
        String path = "tes!t#%Incorrect_Path@#%$";
        PSQLFieldPath.fromString(path);
    }
}
