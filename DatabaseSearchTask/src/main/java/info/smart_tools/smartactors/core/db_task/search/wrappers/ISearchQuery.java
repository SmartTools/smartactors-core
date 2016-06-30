package info.smart_tools.smartactors.core.db_task.search.wrappers;

import info.smart_tools.smartactors.core.db_task.search.utils.IBufferedQuery;
import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.Optional;

/**
 * Interface for message containing incoming search request.
 *
 * Criteria language ("criteria" field contents) rules:
 * <pre>
 *     criteria ::= criteriaGroup
 *
 *     // criteriaGroup which has no compositionType at left side is interpreted as a "$and" criteria group.
 *     criteriaGroup ::= [criteriaGroup, ...]
 *     criteriaGroup ::= {criteriaPair, ...}
 *
 *     // Works only outside a field context.
 *     // Creates a field context with field fieldName for nested criteriaGroup.
 *     criteriaPair ::= fieldName : criteriaGroup
 *
 *     // Works only inside of field context.
 *     criteriaPair ::= constraintName : constraintParams
 *
 *     // Works everywhere.
 *     criteriaPair ::= compositionType : criteriaGroup
 *
 *     constraintName ::= "$eq" | "$gt" | "$lt" | "$tagSet" | "$tagAll" | "$tagAny"
 *
 *     // "$not" actually works as NAND (because right part should always be a criteriaGroup.
 *     compositionType ::= "$or" | "$and" | "$not"
 * </pre>
 */
public interface ISearchQuery {
    void setBufferedQuery(IBufferedQuery query);
    Optional<IBufferedQuery> getBufferedQuery();

    /** The name of the collection to which the query is executed. */
    String getCollectionName();
    void setCollectionName(String collectionName);

    /** Criteria for searching query. */
    Object getCriteria();
    void setCriteria(Object criteria);

    /** Number of pages in searching query. */
    Integer getPageSize();
    void setPageSize(Integer size);

    /** A Page number at which to start searching. */
    Integer getPageNumber();
    void setPageNumber(Integer number);

    /** A search result with found objects. */
    IObject getSearchResult(int index);
    void setSearchResult(Iterable<IObject> documents);
    int countSearchResult();

    /** Order of search result list. */
    IObject getOrderBy(int n);
    int countOrderBy();
    void setOrderBy(Iterable<IObject> order);
}
