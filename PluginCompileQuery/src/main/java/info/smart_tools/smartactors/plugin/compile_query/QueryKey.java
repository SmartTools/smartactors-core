package info.smart_tools.smartactors.plugin.compile_query;

/**
 * Utility class for IOC-strategy for resolving compiled query by connection and query type
 */
final class QueryKey {

    private final String queryType;
    private final String connectionId;

    private QueryKey(final String queryType, final String connectionId) {
        this.queryType = queryType;
        this.connectionId = connectionId;
    }

    static QueryKey create(final String taskName, final String connectionId) {
        return new QueryKey(taskName, connectionId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QueryKey queryKey = (QueryKey) o;

        if (!queryType.equals(queryKey.queryType)) {
            return false;
        }
        return connectionId.equals(queryKey.connectionId);

    }

    @Override
    public int hashCode() {
        int result = queryType.hashCode();
        result = 31 * result + connectionId.hashCode();
        return result;
    }
}
