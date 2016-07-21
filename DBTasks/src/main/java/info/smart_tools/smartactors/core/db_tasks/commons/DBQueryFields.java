package info.smart_tools.smartactors.core.db_tasks.commons;


import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Container of fields for used document fields in message.
 */
public final class DBQueryFields {
    /** Field: "document". */
    public static final IField DOCUMENT;
    /** Field: "collection" in all database query message. */
    public static final IField COLLECTION;
    /** Field: "indexes" in create collection message. */
    public static final IField INDEXES;
    /** Field: "documentId". */
    public static final IField DOCUMENT_ID;
    /** Field: "searchResult" in search query. */
    public static final IField SEARCH_RESULT;
    /** Field: "parameters" in complex query. */
    public static final IField PARAMETERS;
    /** Field: "pageSize" in search query. */
    public static final IField PAGE_SIZE;
    /** Field: "pageNumber" in search query. */
    public static final IField PAGE_NUMBER;
    /** Field: "orderBy" in search query. */
    public static final IField ORDER_BY;
    /** Field: "criteria" in all complex query. */
    public static final IField CRITERIA;

    static {
        try {
            DOCUMENT_ID = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "documentId");
            DOCUMENT = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "document");
            COLLECTION = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "collection");
            INDEXES = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "indexes");
            SEARCH_RESULT = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "searchResult");
            PARAMETERS = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "parameters");
            PAGE_NUMBER = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageNumber");
            PAGE_SIZE = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "pageSize");
            ORDER_BY = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "orderBy");
            CRITERIA = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "criteria");
        } catch (ResolutionException e) {
            throw new RuntimeException("Strategy for resolution of db queries fields doesn't exists!", e);
        }
    }
}
