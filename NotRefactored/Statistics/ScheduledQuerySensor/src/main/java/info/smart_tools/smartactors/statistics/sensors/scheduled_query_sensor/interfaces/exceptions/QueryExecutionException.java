package info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.IQueryExecutor} when error
 * occurs executing query.
 */
public class QueryExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public QueryExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public QueryExecutionException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public QueryExecutionException(final Throwable cause) {
        super(cause);
    }
}
