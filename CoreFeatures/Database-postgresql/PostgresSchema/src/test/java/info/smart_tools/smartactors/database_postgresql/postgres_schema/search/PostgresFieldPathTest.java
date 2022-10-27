package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostgresFieldPathTest {

    @Test
    public void should_ResolvesPostgresFieldPath() throws QueryBuildException {
        String path = "testValidPath";
        FieldPath fieldPath = PostgresFieldPath.fromString(path);
        assertEquals("document#>'{testValidPath}'", fieldPath.toSQL());
    }

    @Test
    public void should_ResolvesPostgresFieldPathWithTypeCast() throws QueryBuildException {
        String path = "testValidPath";
        String type = "int";
        FieldPath fieldPath = PostgresFieldPath.fromStringAndType(path, type);
        assertEquals("(document#>>'{testValidPath}')::int", fieldPath.toSQL());
    }

    @Test(expected = QueryBuildException.class)
    public void should_ThrowsException() throws QueryBuildException {
        String path = "tes!t#%Incorrect_Path@#%$";
        PostgresFieldPath.fromString(path);
    }

    @Test
    public void testComplexPath() throws QueryBuildException {
        String path = "a.b.c";
        FieldPath fieldPath = PostgresFieldPath.fromString(path);
        assertEquals("document#>'{a,b,c}'", fieldPath.toSQL());
    }

    @Test
    public void testComplexPathWithTypeCast() throws QueryBuildException {
        String path = "a.b.c";
        String type = "int";
        FieldPath fieldPath = PostgresFieldPath.fromStringAndType(path, type);
        assertEquals("(document#>>'{a,b,c}')::int", fieldPath.toSQL());
    }

    @Test
    public void testArrayPath() throws QueryBuildException {
        String path = "a[1]";
        FieldPath fieldPath = PostgresFieldPath.fromString(path);
        assertEquals("document#>'{a,1}'", fieldPath.toSQL());
    }

    @Test
    public void testArrayPathAndTypeCast() throws QueryBuildException {
        String path = "a[1]";
        String type = "int";
        FieldPath fieldPath = PostgresFieldPath.fromStringAndType(path, type);
        assertEquals("(document#>>'{a,1}')::int", fieldPath.toSQL());
    }

}
