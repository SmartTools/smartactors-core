package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  Sets some of {@link PreparedStatement}'s parameters starting from {@code firstIndex}.
 *  Returns index of next parameter to be set.
 */
@FunctionalInterface
public interface SQLQueryParameterSetter {
    //TODO: Extend comment
    /**
     * @param statement Statement for using in future
     * @param firstIndex first index
     * @return
     * @throws SQLException Throw when execution statements throw exception
     * @throws QueryBuildException Throw when building query can't be completed
     */
    int setParameters(PreparedStatement statement, int firstIndex) throws SQLException, QueryBuildException;
}
