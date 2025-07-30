package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.exception;

public class CreateCollectionActorException extends Exception {

    public CreateCollectionActorException(final String message) {
        super(message);
    }

    public CreateCollectionActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CreateCollectionActorException(final Throwable cause) {
        super(cause);
    }
}
