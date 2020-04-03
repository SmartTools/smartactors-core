package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.interfaces.idatabase.IDatabase;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of data base on list
 */
public class InMemoryDatabase implements IDatabase {
    private Map<String, List<DataBaseItem>> dataBase = new HashMap<>();
    private IFieldName filterFieldName;

    /**
     * Creates the database.
     * @throws IDatabaseException if not possible to resolve IFieldName
     */
    public InMemoryDatabase() throws IDatabaseException {
        try {
            filterFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "filter");
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to resolve IFieldName", e);
        }
    }

    @Override
    public void upsert(final IObject document, final String collectionName) throws IDatabaseException {
        try {
            DataBaseItem item = IOC.resolve(Keys.getKeyByName(DataBaseItem.class.getCanonicalName()), document, collectionName);
            if (null == item.getId()) {
                insert(item);
            } else {
                update(item);
            }
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to create DataBaseItem", e);
        }
    }

    @Override
    public void createCollection(final String collectionName) {
        dataBase.put(collectionName, new LinkedList<>());
    }

    @Override
    public void insert(final IObject document, final String collectionName) throws IDatabaseException {
        synchronized (this) {
            try {
                DataBaseItem item = IOC.resolve(Keys.getKeyByName(DataBaseItem.class.getCanonicalName()), document, collectionName);
                insert(item);
            } catch (ResolutionException e) {
                throw new IDatabaseException("Failed to create DataBaseItem", e);
            }
        }
    }

    @Override
    public void update(final IObject document, final String collectionName) throws IDatabaseException {
        try {
            DataBaseItem item = IOC.resolve(Keys.getKeyByName(DataBaseItem.class.getCanonicalName()), document, collectionName);
            update(item);
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to create DataBaseItem", e);
        }
    }

    private void update(final DataBaseItem item) throws IDatabaseException {
        if (!dataBase.containsKey(item.getCollectionName())) {
            throw new IDatabaseException("Collection with name " + item.getCollectionName() + " does not exist");
        }
        List<DataBaseItem> list = dataBase.get(item.getCollectionName());
        for (int i = 0; i < list.size(); i++) {
            DataBaseItem inBaseElem = list.get(i);
            if (inBaseElem.getId().equals(item.getId())) {
                list.remove(i);
                list.add(i, item);
            }
        }
    }

    private void insert(final DataBaseItem item) throws IDatabaseException {
        if (!dataBase.containsKey(item.getCollectionName())) {
            throw new IDatabaseException("Collection with name " + item.getCollectionName() + " does not exist");
        }
        try {
            List<DataBaseItem> list = dataBase.get(item.getCollectionName());
            item.setId(nextId(item.getCollectionName()));
            list.add(item);
        } catch (Exception e) {
            throw new IDatabaseException("Failed to insert to DataBaseItem", e);
        }
    }

    @Override
    public IObject getById(final Object id, final String collectionName) throws IDatabaseException {
        if (!dataBase.containsKey(collectionName)) {
            throw new IDatabaseException("Collection with name " + collectionName + " does not exist");
        }
        List<DataBaseItem> list = dataBase.get(collectionName);
        for (DataBaseItem item : list) {
            if (item.getId().equals(id)) {
                return item.getDocument();
            }
        }
        throw new IDatabaseException("There is no element with this id");
    }

    private Object nextId(final String collectionName) throws ResolutionException {
        return IOC.resolve(Keys.getKeyByName("db.collection.nextid"));
    }

    @Override
    public List<IObject> select(final IObject condition, final String collectionName) throws IDatabaseException {
        try {
            List<DataBaseItem> list = dataBase.get(collectionName);
            IObject filter = (IObject) condition.getValue(filterFieldName);
            List<IObject> outputList = new LinkedList<>();
            for (DataBaseItem item : list) {
                if (generalConditionParser(filter, item.getDocument())) {
                    outputList.add(clone(item.getDocument()));
                }
            }
            outputList = IOC.resolve(Keys.getKeyByName("PagingForDatabaseCollection"), condition, outputList);
            outputList = IOC.resolve(Keys.getKeyByName("SortIObjects"), condition, outputList);
            return outputList;
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to resolve IFieldName or PagingForDatabaseCollection or SortIObjects", e);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new IDatabaseException("Failed to get filter from select condition", e);
        }
    }

    private boolean generalConditionParser(final IObject condition, final IObject document) throws IDatabaseException {
        try {
            if (condition == null) {
                return true;
            }
            return IOC.resolve(Keys.getKeyByName("ResolveDataBaseCondition"), "$general_resolver", condition, document);
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to resolve \"ResolveDataBaseCondition\"", e);
        }
    }

    private IObject clone(final IObject iObject) throws IDatabaseException {
        try {
            String serializedIObject = iObject.serialize();
            return IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), serializedIObject);
        } catch (ResolutionException | SerializeException e) {
            throw new IDatabaseException("Failed to clone IObject", e);
        }
    }

    @Override
    public void delete(final IObject document, final String collectionName) throws IDatabaseException {
        try {
            List<DataBaseItem> list = dataBase.get(collectionName);
            DataBaseItem item = IOC.resolve(Keys.getKeyByName(DataBaseItem.class.getCanonicalName()), document, collectionName);
            for (int i = 0; i < list.size(); i++) {
                DataBaseItem inDbItem = list.get(i);
                if (inDbItem.getId().equals(item.getId())) {
                    list.remove(inDbItem);
                    try {
                        document.deleteField(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), collectionName + "ID"));
                    } catch (DeleteValueException | InvalidArgumentException e) {
                        throw new IDatabaseException("Failed to resolve IFieldName", e);
                    } catch (ResolutionException e) {
                        throw new IDatabaseException("Failed to delete field from IObject", e);
                    }
                    return;
                }
            }
        } catch (ResolutionException e) {
            throw new IDatabaseException("Failed to create DataBaseItem", e);
        }
    }

    @Override
    public Long count(final IObject condition, final String collectionName) throws IDatabaseException {
        try {
            List<DataBaseItem> list = dataBase.get(collectionName);
            if (condition == null) {
                return Long.valueOf(list.size());
            }

            IObject filter = (IObject) condition.getValue(filterFieldName);
            long count = 0;
            for (DataBaseItem item : list) {
                if (generalConditionParser(filter, item.getDocument())) {
                    count++;
                }
            }
            return count;
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new IDatabaseException("Failed to get filter from select condition", e);
        }
    }

}
