package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.List;

/**
 * Strategy for getting items from some cache.
 * We may have several objects by one key for current time
 * and user may want to choose one object from result list.
 * This strategy should realize logic for choice.
 */
@FunctionalInterface
public interface GetItemStrategy {

    /**
     * Choose one result object.
     * @param items list of objects from cached collection
     * @return Result IObject
     */
    IObject getItem(List<IObject> items);
}
