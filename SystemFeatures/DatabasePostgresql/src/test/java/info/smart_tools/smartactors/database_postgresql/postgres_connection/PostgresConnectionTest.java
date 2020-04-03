package info.smart_tools.smartactors.database_postgresql.postgres_connection;

import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({PostgresConnection.class, DriverManager.class, Class.class})
@RunWith(PowerMockRunner.class)
public class PostgresConnectionTest {

    private Connection connection;
    private PostgresConnection testPostgresConnection;
    private PreparedStatement validateStatement;

    @Before
    public void before() throws ReadValueException, SQLException, ClassNotFoundException, IOException, StorageException {
        mockStatic(DriverManager.class);
        mockStatic(Class.class);

        ConnectionOptions connectionOptions = mock(ConnectionOptions.class);

        String url = "asd";
        String name = "asdsad";
        String password = "asdasdasd";

        when(connectionOptions.getUrl()).thenReturn(url);
        when(connectionOptions.getUsername()).thenReturn(name);
        when(connectionOptions.getPassword()).thenReturn(password);

        connection = mock(Connection.class);

        when(DriverManager.getConnection(url, name, password)).thenReturn(connection);

        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);

        validateStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement("SELECT(1);")).thenReturn(validateStatement);

        testPostgresConnection = new PostgresConnection(connectionOptions);

        verifyStatic();
        Class.forName("org.postgresql.Driver");

        verifyStatic();
        DriverManager.getConnection(url, name, password);

        verify(connection).setAutoCommit(false);

        verify(connection).prepareStatement("SELECT(1);");
    }

    @Test
    public void MustCorrectValidate() throws SQLException, StorageException {
        when(connection.isClosed()).thenReturn(false);

        assertTrue(testPostgresConnection.validate());

        verify(connection).isClosed();
        verify(validateStatement).executeQuery();
    }

    @Test
    public void MustCorrectValidateWhenFirstExecuteThrowException() throws SQLException, StorageException {
        when(connection.isClosed()).thenReturn(false);

        when(validateStatement.executeQuery()).thenThrow(new SQLException()).thenReturn(null);

        assertTrue(testPostgresConnection.validate());

        verify(connection).isClosed();
        verify(validateStatement, times(2)).executeQuery();
        verify(connection).rollback();
    }

    @Test
    public void MustCorrectValidateWhenIsClosedTrue() throws SQLException, StorageException {
        when(connection.isClosed()).thenReturn(true);

        assertTrue(!testPostgresConnection.validate());

        verify(connection).isClosed();
    }

    @Test
    public void MustInCorrectValidateWhenExecuteTwiceThrowException() throws SQLException, StorageException {
        when(connection.isClosed()).thenReturn(false);

        when(validateStatement.executeQuery()).thenThrow(new SQLException());

        try {
            testPostgresConnection.validate();
        } catch (IllegalArgumentException e) {
            verify(connection).isClosed();
            verify(validateStatement, times(2)).executeQuery();
            verify(connection).rollback();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectValidateWhenIsClosedThrowException() throws SQLException, StorageException {
        when(connection.isClosed()).thenThrow(new SQLException());

        try {
            testPostgresConnection.validate();
        } catch (StorageException e) {
            verify(connection).isClosed();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustCorrectCloseConnection() throws StorageException, SQLException {
        testPostgresConnection.close();
        verify(connection).close();
    }

    @Test
    public void MustInCorrectCloseConnectionWhenCloseThrowException() throws StorageException, SQLException {
        doThrow(new SQLException()).when(connection).close();

        try {
            testPostgresConnection.close();
        } catch (StorageException e) {
            verify(connection).close();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustCorrectCommitConnection() throws StorageException, SQLException {
        testPostgresConnection.commit();
        verify(connection).commit();
    }

    @Test
    public void MustInCorrectCommitConnectionWhenCommitThrowException() throws StorageException, SQLException {
        doThrow(new SQLException()).when(connection).commit();

        try {
            testPostgresConnection.commit();
        } catch (StorageException e) {
            verify(connection).commit();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustCorrectCommitRollback() throws StorageException, SQLException {
        testPostgresConnection.rollback();
        verify(connection).rollback();
    }

    @Test
    public void MustInCorrectRollbackConnectionWhenRollbackThrowException() throws StorageException, SQLException {
        doThrow(new SQLException()).when(connection).rollback();

        try {
            testPostgresConnection.rollback();
        } catch (StorageException e) {
            verify(connection).rollback();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustCorrectCompileQuery() throws Exception {
        QueryStatement statement = mock(QueryStatement.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        JDBCCompiledQuery query = mock(JDBCCompiledQuery.class);

        when(statement.compile(connection)).thenReturn(preparedStatement);
        whenNew(JDBCCompiledQuery.class).withArguments(preparedStatement).thenReturn(query);

        assertTrue(testPostgresConnection.compileQuery(statement) == query);

        verify(statement).compile(connection);
        verifyNew(JDBCCompiledQuery.class).withArguments(preparedStatement);
    }

    @Test
    public void MustInCorrectCompileQueryWhenCompileThrowException() throws Exception {
        QueryStatement statement = mock(QueryStatement.class);

        Writer bodyWriter = mock(Writer.class);
        when(bodyWriter.toString()).thenReturn("as");
        when(statement.getBodyWriter()).thenReturn(bodyWriter);

        when(statement.compile(connection)).thenThrow(new SQLException());

        try {
            testPostgresConnection.compileQuery(statement);
        } catch (StorageException e) {
            verify(statement).compile(connection);
            return;
        }
        assertTrue("Must throw exception", false);
    }

}