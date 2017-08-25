package info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Created by sevenbits on 07.02.17.
 */
public interface GatherWrapper {
    IObject getResult() throws ReadValueException;
}
