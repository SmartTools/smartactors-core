package info.smart_tools.smartactors.core.db_tasks.exception;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;

/**
 *
 */
public class TransactionExecutionException extends Exception {

    private String message;
    private Throwable cause;

    /**
     *
     * @param tasks
     * @param connection
     * @param cause
     */
    public TransactionExecutionException(final IDatabaseTask[] tasks,
                                         final IStorageConnection connection,
                                         final Throwable cause
    ) {
        StringBuilder messageBuilder = new StringBuilder("Error in during transaction for task: [");
                for (IDatabaseTask task : tasks) {
                    messageBuilder.append(task.getClass().getCanonicalName());
                }
                messageBuilder
                        .append("] by connection: [")
                        .append(connection.getClass().getCanonicalName())
                        .append("] with reason: ")
                        .append(cause.getMessage());

        this.message = messageBuilder.toString();
        this.cause = cause;
    }

    public TransactionExecutionException(final IStorageConnection connection,
                                         final Throwable cause
    ) {
        StringBuilder messageBuilder = new StringBuilder("Error in during rollback transaction for connection: [")
                .append(connection.getClass().getCanonicalName())
                .append("] with reason: ")
                .append(cause.getMessage());

        this.message = messageBuilder.toString();
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
