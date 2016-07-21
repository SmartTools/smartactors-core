package info.smart_tools.smartactors.core.db_tasks.commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.ikey.IKey;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * General common thread-unsafe database task for all database oriented tasks uses query cache.
 * @see GeneralDatabaseTask
 */
public abstract class CachedDatabaseTask extends GeneralDatabaseTask {
    /** Common queries cache for all sub task. */
    private static final ConcurrentMap<Integer, ICompiledQuery> CACHED_COMPILED_QUERIES = new ConcurrentHashMap<>();

    /**
     * Default constructor for sub tasks.
     */
    protected CachedDatabaseTask() {
        super();
    }

    /**
     * Gives from queries cache or creates a prepared for execution compiled query with parameters.
     * @see ICompiledQuery
     *
     * @param queryKey - key for searching query in the cache.
     * @param connection - used database connection for compilation query.
     * @param queryStatementBuilder - message with query parameters.
     * @return compiled query for incoming connection.
     * @throws QueryBuildException when errors in during obtaining compiled query.
     */
    protected ICompiledQuery takeCompiledQuery(@Nonnull final IKey queryKey,
                                               @Nonnull final IStorageConnection connection,
                                               @Nonnull final IQueryStatementBuilder queryStatementBuilder
    ) throws QueryBuildException {
        try {
            int hash = queryKey.hashCode();
            ICompiledQuery compiledQuery = CACHED_COMPILED_QUERIES.get(hash);
            if (compiledQuery != null) {
                return compiledQuery;
            }
            compiledQuery = compileQuery(connection, queryStatementBuilder);
            try {
                CACHED_COMPILED_QUERIES.put(hash, compiledQuery);
            } catch (NullPointerException e) {
                throw new QueryBuildException("Query compile error", e);
            }

            return compiledQuery;
        } catch (NullPointerException e) {
            throw new QueryBuildException("Query compile error because: Incoming parameters must not be a null!", e);
        }
    }

    /**
     * Clears queries cache.
     */
    public void clearCache() {
        CACHED_COMPILED_QUERIES.clear();
    }
}
