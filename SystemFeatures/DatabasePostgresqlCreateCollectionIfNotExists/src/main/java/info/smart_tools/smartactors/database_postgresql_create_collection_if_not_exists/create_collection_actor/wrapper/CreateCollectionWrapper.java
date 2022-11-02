package info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.wrapper;

import info.smart_tools.smartactors.database_postgresql_create_collection_if_not_exists.create_collection_actor.CreateCollectionActor;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link CreateCollectionActor#createTable(CreateCollectionWrapper)}.
 */
public interface CreateCollectionWrapper {
    /**
     * @return the collection name which will be created
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    String getCollectionName() throws ReadValueException;

    /**
     * This field allows us to use multiple databases in the project. Just use different connection options!
     *
     * @return the connection options name which is used in the IOC
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    String getConnectionOptionsRegistrationName() throws ReadValueException;

    /**
     * Returns the document describing options of the collection creation.
     *
     * @return the options as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getOptions() throws ReadValueException;
}