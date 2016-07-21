package info.smart_tools.smartactors.core.db_tasks.commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.ComplexCompiledQuery;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IComplexCompiledQuery;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IComplexQueryStatementBuilder;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.IDeclaredParam;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * General common thread-unsafe database task for all database oriented complex tasks with criteria.
 * @see GeneralDatabaseTask
 */
public abstract class ComplexDatabaseTask extends GeneralDatabaseTask {
    /** Parameters handlers. If <code>true</code> then <code>List</code> else <code>Object</code>. */
    private final Map<Boolean, ParameterHandler> handlers;
    /** Common queries cache for all sub tasks. */
    private static final ConcurrentMap<Integer, IComplexCompiledQuery> CACHED_COMPILED_QUERIES;
    /**
     * Max size of queries cache.
     * When the cache of queries reaches its maximum size, it is cleared.
     * @see ComplexDatabaseTask#clearCache()
     */
    private static final int CACHED_MAX_SIZE;

    static {
        try {
            CACHED_COMPILED_QUERIES = new ConcurrentHashMap<>();
            CACHED_MAX_SIZE = IOC.resolve(Keys.getOrAdd("COMPLEX_QUERY_CACHE_SIZE"));
        } catch (ResolutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Clears cache of queries.
     */
    public void clearCache() {
        CACHED_COMPILED_QUERIES.clear();
    }

    /**
     * Default constructor for sub tasks.
     */
    protected ComplexDatabaseTask() {
        super();
        handlers = new HashMap<>(2);
        handlers.put(true, (parameter, sortedParams, declaredParam) -> {
            List<?> inParameters = (List<?>) parameter;
            if (inParameters.size() > declaredParam.getCount()) {
                throw new QueryBuildException("Invalid parameters: too many arguments for \"$in\"!");
            }
            sortedParams.addAll(inParameters);
            int variableSize = declaredParam.getCount() - inParameters.size();
            if (variableSize > 0) {
                for (int i = 0; i < variableSize; ++i) {
                    sortedParams.add(inParameters.get(inParameters.size() - 1));
                }
            }
        });

        handlers.put(false, (parameter, sortedParams, declaredParam) -> sortedParams.add(parameter));
    }

    /**
     * Gives from queries cache or creates a prepared for execution compiled complex query with parameters.
     * @see IComplexCompiledQuery
     * @see ICompiledQuery
     *
     * @param queryKey - key for searching query in the cache.
     * @param connection - used database connection for compilation query.
     * @param queryStatementBuilder - message with query parameters.
     * @return compiled query for incoming connection.
     * @throws QueryBuildException when errors in during obtaining compiled query.
     */
    protected ICompiledQuery takeCompiledQuery(final IKey queryKey,
                                               final IStorageConnection connection,
                                               final IComplexQueryStatementBuilder queryStatementBuilder
    ) throws QueryBuildException {
        List<IDeclaredParam> declaredParams = new ArrayList<>();
        queryStatementBuilder.withDeclaredParams(declaredParams);

        int hash = queryKey.hashCode();
        IComplexCompiledQuery compiledQuery = CACHED_COMPILED_QUERIES.get(hash);
        if (compiledQuery != null) {
            return compiledQuery;
        }
        if (CACHED_COMPILED_QUERIES.size() > CACHED_MAX_SIZE) {
            CACHED_COMPILED_QUERIES.clear();
        }
        compiledQuery = ComplexCompiledQuery.create(
                compileQuery(connection, queryStatementBuilder),
                declaredParams);
        CACHED_COMPILED_QUERIES.put(hash, compiledQuery);

        return compiledQuery;
    }

    /**
     * Sorts incoming parameters from message for complex query with criteria.
     * @see IComplexCompiledQuery
     * @see IDeclaredParam
     *
     * @param query - compiled complex query with order parameters.
     * @see IDeclaredParam
     * @param message - query message with parameters.
     * @return a list of parameters prepared to insert in the query.
     * @throws QueryBuildException when query isn't complex or error in during sorting parameters.
     */
    protected List<Object> sortParameters(@Nonnull final ICompiledQuery query,
                                          @Nonnull final IObject message)
            throws QueryBuildException {
        try {
            IComplexCompiledQuery searchCompiledQuery = (IComplexCompiledQuery) query;
            IObject parameters = DBQueryFields.PARAMETERS.in(message);
            List<IDeclaredParam> declaredParams = searchCompiledQuery.getDeclaredParams();

            List<Object> sortedParams = new ArrayList<>();
            for (IDeclaredParam declaredParam : declaredParams) {
                Object parameter = parameters.getValue(declaredParam.getName());
                handlers.get(List.class.isAssignableFrom(parameter.getClass()))
                        .handle(parameter, sortedParams, declaredParam);
            }

            return  sortedParams;
        } catch (NullPointerException e) {
            throw new QueryBuildException("Sorted parameters error because: Incoming parameters must not be a null!", e);
        } catch (ClassCastException e) {
            throw new QueryBuildException("Expected complex query!");
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new QueryBuildException(e.getMessage(), e);
        }
    }

    /**
     * Functional interface for strategies of handling parameters.
     */
    @FunctionalInterface
    private interface ParameterHandler {
        /**
         * Handle incoming parameter.
         * Incoming parameter must match declared parameter.
         *
         * @param parameter - handling parameter.
         * @param sortedParams - list with prepared sorted parameters.
         * @param declaredParam - declared parameter.
         * @throws QueryBuildException when error in during handling parameter.
         */
        void handle(final Object parameter, final List<Object> sortedParams, final IDeclaredParam declaredParam)
                throws QueryBuildException;
    }
}
