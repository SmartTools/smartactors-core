package info.smart_tools.smartactors.in_memory_database_service_starter.in_memory_database_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.database.interfaces.idatabase.IDatabase;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.database_in_memory.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.List;

/**
 * Processing strategy for create and fill {@link InMemoryDatabase}
 * <p>
 * <pre>
 *   {
 *       "inMemoryDb": [
 *             {
 *                 "name": "my_collection_name",
 *                 "documents": [
 *                      "{\"foo\": \"bar\"}",
 *                      "{\"foo1\": \"bar1\"}"
 *                 ]
 *             },
 *             {
 *                 // . . .
 *             }
 *         ]
 *     }
 * </pre>
 */
public class InMemoryDBSectionProcessingStrategy implements ISectionStrategy {

    private final IFieldName name;
    private final IFieldName nameFieldName;
    private final IFieldName documentsFieldName;

    /**
     * Constructor
     * @throws ResolutionException if fails to resolve any dependencies
     */
    InMemoryDBSectionProcessingStrategy() throws ResolutionException {
        this.name = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "inMemoryDb");
        this.nameFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "name"
        );
        this.documentsFieldName = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                "documents"
        );
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            List<IObject> databaseObject = (List<IObject>) config.getValue(name);
            IDatabase dataBase = IOC.resolve(Keys.getKeyByName(InMemoryDatabase.class.getCanonicalName()));
            for (IObject collection : databaseObject) {
                String collectionName = (String) collection.getValue(nameFieldName);
                dataBase.createCollection(collectionName);
                List<String> documents = (List<String>) collection.getValue(documentsFieldName);
                for (String document : documents) {
                    dataBase.insert(IOC.resolve(Keys.getKeyByName("IObjectByString"), document), collectionName);
                }
            }
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"inMemoryDb\" configuration section.", e);
        } catch (ResolutionException e) {
            throw new ConfigurationProcessingException("Error occurred resolving \"InMemoryDatabase\".", e);
        } catch (IDatabaseException e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config) throws ConfigurationProcessingException {
        // ToDo: write corresponding revert code
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }

    @Override
    public String getSectionSchema() {
        return null;
    }
}
