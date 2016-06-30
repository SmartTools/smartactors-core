package info.smart_tools.smartactors.core.cached_collection;

import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.List;

/**
 * Strategy for getting items from some db
 */
@FunctionalInterface
public interface GetItemStrategy {

    /**
     * @param items Params
     * @return Result IObject
     */
    IObject getItem(List<IObject> items);
}
