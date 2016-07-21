package info.smart_tools.smartactors.core.db_storage.interfaces;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  Sets some of {@link PreparedStatement}'s parameters starting from {@code firstIndex}.
 *  Returns index of next parameter to be set.
 */
@FunctionalInterface
public interface ISQLQueryParameterSetter {
    /**
     * @param statement Statement for using in future
     * @return
     * @throws SQLException Throw when execution statements throw exception
     * @throws QueryBuildException Throw when building query can't be completed
     */
    void setParameters(PreparedStatement statement) throws SQLException, QueryBuildException;
}
