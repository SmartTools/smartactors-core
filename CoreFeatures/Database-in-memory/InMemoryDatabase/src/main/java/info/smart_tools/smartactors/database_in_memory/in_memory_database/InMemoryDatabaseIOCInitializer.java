package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_strategy_pack.uuid_nextid_strategy.UuidNextIdStrategy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class to put all necessary dependencies into IOC for InMemoryDatabase to work correctly.
 */
public final class InMemoryDatabaseIOCInitializer {

    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Private constructor to avoid instantiation.
     */
    private InMemoryDatabaseIOCInitializer() {
    }

    /**
     * Initializes IOC strategies.
     * @throws Exception if anything goes wrong
     */
    public static void init() throws Exception {
        Map<String, IConditionVerifier> verifierMap = new HashMap<>();
        registerNextIdStrategy();
        registerCompareSimpleObjects();
        registerNestedFieldName();
        initVerifierMap(verifierMap);
        registerVerifierMapCalls(verifierMap);
        registerPagingForDatabaseCollection();
        registerSortIObjects();
        registerDataBaseItem();
        registerInMemoryDatabase();
    }

    private static void registerNextIdStrategy() throws ResolutionException, RegistrationException {
        IOC.register(Keys.getKeyByName("db.collection.nextid"), new UuidNextIdStrategy());
    }

    private static void registerCompareSimpleObjects() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("CompareSimpleObjects"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            Object entry = args[0];
                            Object reference = args[1];
                            if (null == entry) {
                                return -1;
                            }
                            if (null == reference) {
                                return 1;
                            }
                            if (reference instanceof Long || reference instanceof Integer) {
                                Long longEntry = Long.parseLong(String.valueOf(entry));
                                Long longReference = Long.parseLong(String.valueOf(reference));
                                return -longReference.compareTo(longEntry);
                            }
                            if (reference instanceof Double) {
                                Double doubleReference = (Double) reference;
                                Double doubleEntry = Double.parseDouble(entry.toString());
                                return -doubleReference.compareTo(doubleEntry);
                            }
                            if (reference instanceof String) {
                                String doubleEntry = (String) entry;
                                String doubleReference = (String) reference;
                                return -doubleReference.compareTo(doubleEntry);
                            }
                            return 0;
                        }
                )
        );
    }

    private static void registerNestedFieldName() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("NestedFieldName"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IFieldName fieldName = (IFieldName) args[0];
                            IObject iObject = (IObject) args[1];
                            String string = fieldName.toString();
                            String[] strings = string.split("\\.");
                            Object bufObject = iObject;
                            try {
                                for (String fieldString : strings) {
                                    if (!(bufObject instanceof IObject)) {
                                        return null;
                                    }
                                    IFieldName bufFieldName =
                                            IOC.resolve(
                                                    Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                                    fieldString
                                            );
                                    bufObject = ((IObject) bufObject).getValue(bufFieldName);
                                    if (null == bufObject) {
                                        return null;
                                    }
                                }
                                return bufObject;
                            } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                            }
                            return null;
                        }
                )
        );
    }

    private static void initVerifierMap(final Map<String, IConditionVerifier> verifierMap) {
        initGeneralResolver(verifierMap);
        initEqOperations(verifierMap);
        initGtLtOperations(verifierMap);
        initDateOperations(verifierMap);
        initInOperation(verifierMap);
        initIsNullOperation(verifierMap);
        initHasTagOperation(verifierMap);
        initFullTextOperation(verifierMap);
        initConditions(verifierMap);
    }

    private static void initGeneralResolver(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$general_resolver", (condition, document) -> {
                    Iterator<Map.Entry<IFieldName, Object>> mainIterator = condition.iterator();
                    List<IObject> and = new LinkedList<>();
                    while (mainIterator.hasNext()) {
                        Map.Entry<IFieldName, Object> entry = mainIterator.next();
                        try {
                            IObject iObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
                            iObject.setValue(entry.getKey(), entry.getValue());
                            and.add(iObject);
                        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
                        }
                    }
                    IObject newCondition = condition;
                    if (and.size() > 1) {
                        IFieldName andFieldName;
                        try {
                            newCondition = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
                            andFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "$and");
                            newCondition.setValue(andFieldName, and);
                        } catch (ResolutionException | InvalidArgumentException | ChangeValueException e) {
                        }
                    }
                    condition = newCondition;
                    Iterator<Map.Entry<IFieldName, Object>> iterator = condition.iterator();

                    if (!iterator.hasNext()) {
                        return true;
                    }

                    String key = null;
                    do {
                        Map.Entry<IFieldName, Object> entry = iterator.next();
                        key = entry.getKey().toString();
                        if (entry.getValue() instanceof IObject) {
                            iterator = ((IObject) entry.getValue()).iterator();
                        }
                    } while (iterator.hasNext() && !verifierMap.containsKey(key));
                    return verifierMap.get(key).verify(condition, document);
                }
        );
    }

    private static void initEqOperations(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$eq", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(Keys.getKeyByName("NestedFieldName"), fieldName, document);
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$eq"));
                        return reference.equals(entry);
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
        verifierMap.put("$neq", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(Keys.getKeyByName("NestedFieldName"), fieldName, document);
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$neq"));
                        return !reference.equals(entry);
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
    }

    private static void initGtLtOperations(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$gt", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(Keys.getKeyByName("NestedFieldName"), fieldName, document);
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$gt"));
                        return ((Integer) IOC.resolve(
                                Keys.getKeyByName("CompareSimpleObjects"), entry, reference)
                        ) > 0;
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
        verifierMap.put("$lt", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(Keys.getKeyByName("NestedFieldName"), fieldName, document);
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$lt"));
                        return (Integer) IOC.resolve(
                                Keys.getKeyByName("CompareSimpleObjects"), entry, reference
                        ) < 0;
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
        verifierMap.put("$gte", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(Keys.getKeyByName("NestedFieldName"), fieldName, document);
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$gte"));
                        return (Integer) IOC.resolve(
                                Keys.getKeyByName("CompareSimpleObjects"), entry, reference
                        ) >= 0;
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
        verifierMap.put("$lte", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(
                                Keys.getKeyByName("NestedFieldName"),
                                fieldName,
                                document
                        );
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$lte"));
                        return (Integer) IOC.resolve(
                                Keys.getKeyByName("CompareSimpleObjects"), entry, reference
                        ) <= 0;
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
    }

    private static void initDateOperations(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$date-from", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        String entry = (String) document.getValue(fieldName);
                        String reference = String.valueOf(
                                ((IObject) condition.getValue(fieldName))
                                        .getValue(new FieldName("$date-from"))
                        );
                        return reference.compareTo(entry) == -1 || reference.equals(entry);
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return false;
                }
        );
        verifierMap.put("$date-to", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        String entry = (String) document.getValue(fieldName);
                        String reference = String.valueOf(
                                ((IObject) condition.getValue(fieldName))
                                        .getValue(new FieldName("$date-to"))
                        );
                        return reference.compareTo(entry) == 1 || reference.equals(entry);
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return false;
                }
        );
    }

    private static void initInOperation(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$in", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(
                                Keys.getKeyByName("NestedFieldName"),
                                fieldName,
                                document
                        );
                        List<Object> references = (List<Object>)
                                ((IObject) condition.getValue(fieldName))
                                        .getValue(new FieldName("$in"));
                        for (Object reference : references) {
                            if (reference.equals(entry)) {
                                return true;
                            }
                        }
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
    }

    private static void initIsNullOperation(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$isNull", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(
                                Keys.getKeyByName("NestedFieldName"),
                                fieldName,
                                document
                        );
                        Boolean references = (Boolean)
                                ((IObject) condition.getValue(fieldName))
                                        .getValue(new FieldName("$isNull"));
                        return (null == entry) == references;
                    } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                    }
                    return false;
                }
        );
    }

    private static void initHasTagOperation(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$hasTag", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(
                                Keys.getKeyByName("NestedFieldName"),
                                fieldName,
                                document
                        );
                        if (null == entry) {
                            return false;
                        }
                        Object reference = ((IObject) condition.getValue(fieldName))
                                .getValue(new FieldName("$hasTag"));
                        if (entry instanceof List) {
                            List<Object> entryList = (List<Object>) entry;
                            for (Object entryItem : entryList) {
                                if (entryItem.equals(reference)) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        IFieldName tagFieldName = IOC.resolve(
                                Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                reference
                        );
                        return null != ((IObject) entry).getValue(tagFieldName);
                    } catch (Exception e) {
                        return false;
                    }
                }
        );
    }

    private static void initFullTextOperation(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$fulltext", (condition, document) -> {
                    IFieldName fieldName = condition.iterator().next().getKey();
                    try {
                        Object entry = IOC.resolve(
                                Keys.getKeyByName("NestedFieldName"),
                                fieldName,
                                document
                        );
                        if (null == entry) {
                            return false;
                        }
                        String reference = String.valueOf(
                                ((IObject) condition.getValue(fieldName))
                                        .getValue(new FieldName("$fulltext"))
                        );
                        SimpleFullTextMatcher matcher = new SimpleFullTextMatcher(reference);
                        return matcher.matches(String.valueOf(entry));
                    } catch (Exception e) {
                        return false;
                    }
                }
        );
    }

    private static void initConditions(final Map<String, IConditionVerifier> verifierMap) {
        verifierMap.put("$and", (condition, document) -> {
                    boolean result = true;
                    try {
                        List<IObject> conditions = (List<IObject>) condition
                                .getValue(new FieldName("$and"));
                        for (IObject conditionItem : conditions) {
                            result &= verifierMap.get("$general_resolver")
                                    .verify(conditionItem, document);
                        }
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return result;
                }
        );
        verifierMap.put("$or", (condition, document) -> {
                    boolean result = false;
                    try {
                        List<IObject> conditions = (List<IObject>) condition
                                .getValue(new FieldName("$or"));
                        for (IObject conditionItem : conditions) {
                            result |= verifierMap.get("$general_resolver")
                                    .verify(conditionItem, document);
                        }
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return result;
                }
        );
        verifierMap.put("$not", (condition, document) -> {
                    boolean result = false;
                    try {
                        List<IObject> conditions = (List<IObject>) condition
                                .getValue(new FieldName("$not"));
                        for (IObject conditionItem : conditions) {
                            // using De Morgan's laws: !(A & B) == !A | !B
                            result |= !verifierMap.get("$general_resolver")
                                    .verify(conditionItem, document);
                        }
                    } catch (ReadValueException | InvalidArgumentException e) {
                    }
                    return result;
                }
        );
    }

    private static void registerVerifierMapCalls(final Map<String, IConditionVerifier> verifierMap)
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("ResolveDataBaseCondition"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                verifierMap.get(args[0])
                                        .verify((IObject) args[1], (IObject) args[2])
                )
        );

        IOC.register(Keys.getKeyByName("ContainsResolveDataBaseCondition"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) ->
                                verifierMap.containsKey(args[0])
                )
        );
    }

    private static void registerPagingForDatabaseCollection() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("PagingForDatabaseCollection"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            Integer pageNumber = 1;
                            Integer pageSize = DEFAULT_PAGE_SIZE;
                            try {
                                IFieldName pageFieldName = IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                        "page"
                                );
                                IFieldName pageNumberFieldName = IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                        "number"
                                );
                                IFieldName pageSizeFieldName = IOC.resolve(
                                        Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                                        "size"
                                );
                                IObject page = (IObject) ((IObject) args[0]).getValue(pageFieldName);
                                if (null != page) {
                                    pageNumber = (Integer) page.getValue(pageNumberFieldName);
                                    pageSize = (Integer) page.getValue(pageSizeFieldName);
                                }
                            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                            }
                            Integer startNumber = (pageNumber - 1) * pageSize;
                            Integer finNumber = pageNumber * pageSize;
                            List<IObject> outputDocuments = new LinkedList<>();
                            List<IObject> documents = (List<IObject>) args[1];
                            for (int i = startNumber; i < finNumber && i < documents.size(); i++) {
                                outputDocuments.add(documents.get(i));
                            }
                            return outputDocuments;
                        }
                )
        );
    }

    private static void registerSortIObjects() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("SortIObjects"), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            List<IObject> sortRules = null;
                            List<IObject> documents = (List<IObject>) args[1];

                            try {
                                IObject condition = (IObject) args[0];
                                IFieldName sortFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sort");
                                sortRules = (List<IObject>) condition.getValue(sortFieldName);
                            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                                // ignoring absence of sort rule
                            }

                            if (null == sortRules) {
                                return documents;
                            }

                            List<IFieldName> sortingFields = new LinkedList<>();
                            List<Integer> sortingType = new LinkedList<>();
                            for (IObject sortRule : sortRules) {
                                sortingFields.add(sortRule.iterator().next().getKey());
                                sortingType.add(sortRule.iterator().next().getValue().equals("asc") ? 1 : -1);
                            }
                            Comparator comparator = (o1, o2) -> {
                                try {
                                    Integer compare = 0;
                                    for (int i = 0; i < sortingFields.size(); i++) {
                                        Object object1 = IOC.resolve(Keys.getKeyByName("NestedFieldName"), sortingFields.get(i), o1);
                                        Object object2 = IOC.resolve(Keys.getKeyByName("NestedFieldName"), sortingFields.get(i), o2);
                                        compare = IOC.resolve(Keys.getKeyByName("CompareSimpleObjects"), object1, object2);
                                        if (compare != 0) {
                                            return compare * sortingType.get(i);
                                        }
                                    }
                                } catch (ResolutionException e) {
                                }
                                return 0;
                            };
                            documents.sort(comparator);
                            return documents;

                        }
                )
        );
    }

    private static void registerDataBaseItem() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName(DataBaseItem.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new DataBaseItem((IObject) args[0], (String) args[1]);
                            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                            }
                            return null;
                        }
                )
        );
    }

    private static void registerInMemoryDatabase()
            throws IDatabaseException, RegistrationException, ResolutionException, InvalidArgumentException {
        InMemoryDatabase database = new InMemoryDatabase();
        IOC.register(
                Keys.getKeyByName(InMemoryDatabase.class.getCanonicalName()),
                new SingletonStrategy(database)
        );
    }



}
