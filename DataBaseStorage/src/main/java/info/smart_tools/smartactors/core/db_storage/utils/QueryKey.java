package info.smart_tools.smartactors.core.db_storage.utils;

import info.smart_tools.smartactors.core.ikey.IKey;

/**
 * Utility class for IOC-strategy for resolving compiled query by connection and query type
 */
final public class QueryKey implements IKey {
    private String connectionId;
    private String queryType;
    private String collection;
    private int uniqueCode;

    private QueryKey(
            final String connectionId,
            final String queryType,
            final String collection,
            final int uniqueCode
    ) {
        this.connectionId = connectionId;
        this.queryType = queryType;
        this.collection = collection;
        this.uniqueCode = uniqueCode;
    }

    private QueryKey(
            final String connectionId,
            final String queryType,
            final String collection
    ) {
        this.connectionId = connectionId;
        this.queryType = queryType;
        this.collection = collection;
        this.uniqueCode = Integer.MIN_VALUE;
    }

    public static QueryKey create(
            final String connectionId,
            final String taskName,
            final String collection,
            final int uniqueCode
    ) {
        return new QueryKey(connectionId, taskName, collection, uniqueCode);
    }

    public static QueryKey create(
            final String connectionId,
            final String taskName,
            final String collection
    ) {
        return new QueryKey(connectionId, taskName, collection);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryKey queryKey = (QueryKey) o;

        return connectionId.equals(queryKey.connectionId) && queryType.equals(queryKey.queryType) &&
                collection.equals(queryKey.collection) && uniqueCode == queryKey.uniqueCode;
    }

    @Override
    public int hashCode() {
        return  31 * connectionId.hashCode() + queryType.hashCode() +
                collection.hashCode() + uniqueCode;
    }
}
