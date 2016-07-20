package info.smart_tools.smartactors.core.postgres_connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  Sets some of {@link PreparedStatement}'s parameters starting from {@code firstIndex}.
 *  Returns index of next parameter to be set.
 */
@FunctionalInterface
public interface SQLQueryParameterSetter {
    /**
     * Sets some parameters to statement
     * @param statement the statement where parameters are sets
     * @param firstIndex the first index
     * @return index of next parameter to be set
     * @throws SQLException throw when can't handle statement
     */
    int setParameters(PreparedStatement statement, int firstIndex) throws SQLException;
}
